package com.cortis.agent.chat;

import com.cortis.agent.chat.AgentChatDtos.*;
import com.cortis.auth.security.CortisUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** Session-authenticated HTTP API; agent runs as the CURRENT USER, not as admin. */
@RestController
@RequestMapping("/api/agent/conversations")
@PreAuthorize("isAuthenticated()")
public class CortisAgentController {
    private final AgentChatRepository repository;
    private final CortisAgentService agent;

    public CortisAgentController(AgentChatRepository repository, CortisAgentService agent) {
        this.repository = repository;
        this.agent = agent;
    }

    @GetMapping
    public List<Conversation> list(@AuthenticationPrincipal CortisUserPrincipal user) {
        return repository.conversations(user.getUserId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Conversation create(@AuthenticationPrincipal CortisUserPrincipal user) {
        return repository.create(user.getUserId());
    }

    @GetMapping("/{id}/messages")
    public List<Message> messages(@PathVariable UUID id, Authentication authentication) {
        return repository.messages(id, authentication);
    }

    @PostMapping("/{id}/messages")
    public Answer send(@PathVariable UUID id, @Valid @RequestBody Send body,
                       Authentication authentication) {
        return agent.send(id, body.message(), authentication);
    }
}
