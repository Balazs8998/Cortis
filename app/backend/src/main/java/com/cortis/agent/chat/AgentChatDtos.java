package com.cortis.agent.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class AgentChatDtos {
    private AgentChatDtos() {}

    public record Conversation(UUID id, String title, Instant updatedAt) {}
    public record Message(String role, String content, Instant createdAt) {}
    public record Send(@NotBlank @Size(max=3000) String message) {}
    public record Answer(String message, int iterations, int toolCalls, String terminationReason,
                         List<ToolEvent> tools) {}
    public record ToolEvent(String name, String status, String note) {}
}
