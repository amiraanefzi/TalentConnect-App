package tn.iteam.chatbotservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @JsonProperty("sender")
    private String sender; // "user" or "bot"

    @JsonProperty("message")
    private String message;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("timestamp")
    private long timestamp;

}

