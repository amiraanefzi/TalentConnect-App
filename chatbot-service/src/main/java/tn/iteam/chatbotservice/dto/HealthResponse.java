package tn.iteam.chatbotservice.dto;

import java.time.Instant;

public record HealthResponse(String status, String service, Instant checkedAt) {
}
