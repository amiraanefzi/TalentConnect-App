package tn.iteam.chatbotservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank @Size(max = 100) String userId,
        @NotBlank @Size(max = 2000) String message
) {
}
