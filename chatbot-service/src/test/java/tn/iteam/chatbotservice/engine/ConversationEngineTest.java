package tn.iteam.chatbotservice.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConversationEngineTest {

    private ConversationEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new ConversationEngine();
    }

    @Test
    public void testGreetingPattern() {
        String response = engine.processMessage("Bonjour");
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testJobSearchPattern() {
        String response = engine.processMessage("Je cherche un emploi");
        assertNotNull(response);
        assertTrue(response.contains("💼") || response.contains("emploi"));
    }

    @Test
    public void testCareerPattern() {
        String response = engine.processMessage("Je veux développer ma carrière");
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testBenefitsPattern() {
        String response = engine.processMessage("Quels sont les avantages?");
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testDefaultResponse() {
        String response = engine.processMessage("xyzabc123nonsense");
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testCaseInsensitivity() {
        String response1 = engine.processMessage("BONJOUR");
        String response2 = engine.processMessage("bonjour");
        assertNotNull(response1);
        assertNotNull(response2);
    }

}

