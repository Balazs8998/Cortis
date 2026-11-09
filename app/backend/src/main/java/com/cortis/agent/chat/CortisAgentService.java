package com.cortis.agent.chat;

import com.cortis.agent.chat.AgentChatDtos.*;
import com.cortis.agent.chat.AgentChatRepository.StoredMessage;
import com.cortis.auth.security.CortisUserPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** A bounded, inspectable Ollama agent loop using only permission-checked read tools. */
@Service
public class CortisAgentService {
    static final int MAX_TOOLS = 4;
    static final int MAX_ITERATIONS = 4;
    private final AgentChatRepository repository;
    private final CortisOllamaGateway ollama;
    private final AgentReadTools tools;
    private final ObjectMapper mapper;

    public CortisAgentService(AgentChatRepository repository, CortisOllamaGateway ollama,
                              AgentReadTools tools, ObjectMapper mapper) {
        this.repository = repository;
        this.ollama = ollama;
        this.tools = tools;
        this.mapper = mapper;
    }

    public Answer send(UUID id, String message, Authentication authentication) {
        repository.verify(id, authentication);
        if (!(authentication.getPrincipal() instanceof CortisUserPrincipal principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        String text = message.strip();
        if (text.isEmpty() || text.length() > 3000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message must contain 1 to 3000 characters");
        }
        UUID runId = repository.beginRun(id, principal.getUserId(), ollama.model(), MAX_TOOLS, MAX_ITERATIONS);
        long started = System.nanoTime();
        int toolCalls = 0;
        int iterations = 0;
        List<ToolEvent> events = new ArrayList<>();
        try {
            repository.append(id, "USER", text, null, null);
            repository.titleOnce(id, text);
            List<Map<String, Object>> permittedTools = tools.definitions(authentication);
            boolean budgetExceeded = false;
            boolean finalOnly = false;
            for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
                iterations++;
                JsonNode reply = ollama.chat(repository.modelHistory(id), (budgetExceeded || finalOnly) ? List.of() : permittedTools);
                String content = reply.path("content").asText("").strip();
                JsonNode requested = reply.path("tool_calls");
                boolean hasCalls = requested.isArray() && !requested.isEmpty();
                repository.append(id, "ASSISTANT", content.isEmpty() ? null : content,
                        null, hasCalls ? asString(requested) : null);
                if (!hasCalls) {
                    if (content.isBlank()) {
                        content = "A modell nem küldött szöveges választ.";
                        repository.append(id, "ASSISTANT", content, null, null);
                    }
                    boolean incomplete = budgetExceeded || events.stream().anyMatch(event -> !"SUCCESS".equals(event.status()));
                    String reason = budgetExceeded ? "TOOL_LIMIT" : incomplete ? "TOOL_FAILURE" : "COMPLETED";
                    repository.finish(runId, incomplete ? "PARTIAL" : "COMPLETED",
                            reason, toolCalls, iterations, elapsed(started), null);
                    return new Answer(content, iterations, toolCalls, reason, events);
                }
                if (requested.size() > 12) {
                    throw new IllegalStateException("Model requested too many tools in one response");
                }
                for (JsonNode call : requested) {
                    String name = call.path("function").path("name").asText("");
                    JsonNode args = call.path("function").path("arguments");
                    if (!args.isObject()) args = mapper.createObjectNode();
                    long toolStarted = System.nanoTime();
                    String status = "SUCCESS";
                    String errorCode = null;
                    JsonNode result;
                    if (toolCalls >= MAX_TOOLS || budgetExceeded || finalOnly) {
                        budgetExceeded = true;
                        status = "SKIPPED";
                        errorCode = "TOOL_LIMIT";
                        result = mapper.valueToTree(Map.of("error", "Tool budget exceeded; report incomplete checks"));
                    } else {
                        toolCalls++;
                        try {
                            if (!permittedName(permittedTools, name)) {
                                status = "REJECTED";
                                errorCode = "TOOL_NOT_ALLOWED";
                                result = mapper.valueToTree(Map.of("error", "Tool not available to this user"));
                            } else {
                                result = tools.execute(name, args, authentication);
                            }
                        } catch (RuntimeException exception) {
                            status = "FAILED";
                            errorCode = "TOOL_FAILED";
                            // Never send internal SQL errors, stack traces or filesystem details to the model.
                            result = mapper.valueToTree(Map.of("error", "The requested tool could not return data"));
                        }
                    }
                    repository.append(id, "TOOL", asString(result), name, null);
                    repository.logTool(runId, events.size() + 1, safeName(name), asString(args),
                            asString(result), status, errorCode, elapsed(toolStarted));
                    events.add(new ToolEvent(safeName(name), status, status.equals("SUCCESS") ? "Lekérdezés kész" : "Nem teljesült"));
                }
                if (toolCalls >= MAX_TOOLS) budgetExceeded = true;
                if (iteration == MAX_ITERATIONS - 2) finalOnly = true;
            }
            // Never leave the run active; return a safe partial result if the model loops indefinitely.
            String fallback = "Elértem az agent végrehajtási limitjét. Az ellenőrzés nem teljes; ne tekintsd a gépösszeállítást jóváhagyottnak.";
            repository.append(id, "ASSISTANT", fallback, null, null);
            repository.finish(runId, "PARTIAL", "ITERATION_LIMIT", toolCalls, iterations, elapsed(started), null);
            return new Answer(fallback, iterations, toolCalls, "ITERATION_LIMIT", events);
        } catch (RestClientException failure) {
            repository.finish(runId, "FAILED", "OLLAMA_UNAVAILABLE", toolCalls, iterations,
                    elapsed(started), "OLLAMA_UNAVAILABLE");
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "A helyi Ollama nem érhető el. Ellenőrizd a szolgáltatást és a modellt.");
        } catch (RuntimeException failure) {
            repository.finish(runId, "FAILED", "AGENT_FAILURE", toolCalls, iterations,
                    elapsed(started), "AGENT_FAILURE");
            throw failure;
        }
    }

    static boolean permittedName(List<Map<String, Object>> definitions, String name) {
        return definitions.stream().anyMatch(d -> {
            Object fn = d.get("function");
            return fn instanceof Map<?, ?> map && name.equals(map.get("name"));
        });
    }

    private static String safeName(String input) {
        if (input == null || input.isBlank()) return "unknown_tool";
        return input.length() > 100 ? input.substring(0, 100) : input;
    }

    private String asString(JsonNode node) {
        try { return mapper.writeValueAsString(node); }
        catch (JsonProcessingException e) { throw new IllegalStateException("Cannot serialize model data", e); }
    }

    private static long elapsed(long sinceNano) {
        return Math.max(0, (System.nanoTime() - sinceNano) / 1_000_000L);
    }
}
