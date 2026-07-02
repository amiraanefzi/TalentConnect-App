package tn.iteam.chatbotservice.client.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserSummary(
    Long id,
    String email,
    String firstName,
    String lastName,
    String department,
    String title,
    Set<String> roles,
    List<String> skills
) {}

