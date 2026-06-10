package tn.iteam.chatbotservice.engine;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatbotEngineTest {

    private final ChatbotEngine engine = new ChatbotEngine();

    @Test
    void detectsJobSearchIntent() {
        BotReply reply = engine.replyTo("Je cherche un emploi a Tunis");

        assertThat(reply.intent()).isEqualTo("job_search");
        assertThat(reply.message()).isNotBlank();
    }

    @Test
    void normalizesAccentsForCareerIntent() {
        BotReply reply = engine.replyTo("Je veux developper ma carriere");

        assertThat(reply.intent()).isEqualTo("career");
    }

    @Test
    void returnsFallbackForUnknownMessage() {
        BotReply reply = engine.replyTo("xyzabc123");

        assertThat(reply.intent()).isEqualTo("fallback");
        assertThat(reply.message()).isNotBlank();
    }

    @Test
    void returnsDeterministicResponses() {
        BotReply first = engine.replyTo("bonjour");
        BotReply second = engine.replyTo("bonjour");

        assertThat(second).isEqualTo(first);
    }
}
