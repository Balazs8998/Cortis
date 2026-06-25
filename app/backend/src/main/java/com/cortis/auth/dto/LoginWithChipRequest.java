package com.cortis.auth.dto;

import jakarta.validation.constraints.NotBlank;


public record LoginWithChipRequest(
        @NotBlank(message = "Chip code cannot be blank")
        String chipCode
) {
}
