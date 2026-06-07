package tn.iteam.chatbotservice.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Random;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationPattern {

    private List<String> keywords;
    private List<String> responses;
    private Random random = new Random();

    public boolean matches(String message) {
        return keywords.stream()
                .anyMatch(message::contains);
    }

    public String getRandomResponse() {
        return responses.get(random.nextInt(responses.size()));
    }

}

