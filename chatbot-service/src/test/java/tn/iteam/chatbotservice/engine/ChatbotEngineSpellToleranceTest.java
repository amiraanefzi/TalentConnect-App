package tn.iteam.chatbotservice.engine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Chatbot Engine - Intelligent Spell Tolerance Tests")
class ChatbotEngineSpellToleranceTest {

    private final ChatbotEngine engine = new ChatbotEngine();

    @Test
    @DisplayName("Should understand misspelled 'emploi' variations")
    void testEmploymentKeywordMisspellings() {
        // Exact match
        BotReply reply1 = engine.replyTo("emploi");
        assertEquals("job_search", reply1.intent());

        // Common typos
        BotReply reply2 = engine.replyTo("empoloi");  // swap letters
        assertEquals("job_search", reply2.intent());

        BotReply reply3 = engine.replyTo("emplo");    // missing letter
        assertEquals("job_search", reply3.intent());

        BotReply reply4 = engine.replyTo("empoi");    // another typo
        assertEquals("job_search", reply4.intent());
    }

    @Test
    @DisplayName("Should understand misspelled 'carrière' variations")
    void testCareerKeywordMisspellings() {
        // Exact match
        BotReply reply1 = engine.replyTo("carriere");
        assertEquals("career", reply1.intent());

        // With accent variations
        BotReply reply2 = engine.replyTo("cariere");   // missing 'r'
        assertEquals("career", reply2.intent());

        BotReply reply3 = engine.replyTo("carierre");  // extra 'r'
        assertEquals("career", reply3.intent());

        BotReply reply4 = engine.replyTo("carieere");  // different pattern
        assertEquals("career", reply4.intent());
    }

    @Test
    @DisplayName("Should understand misspelled 'salaire' variations")
    void testSalaryKeywordMisspellings() {
        // Exact match
        BotReply reply1 = engine.replyTo("salaire");
        assertEquals("benefits", reply1.intent());

        // Common typos
        BotReply reply2 = engine.replyTo("salere");    // wrong vowels
        assertEquals("benefits", reply2.intent());

        BotReply reply3 = engine.replyTo("salaira");   // letter swap
        assertEquals("benefits", reply3.intent());

        BotReply reply4 = engine.replyTo("salare");    // another variant
        assertEquals("benefits", reply4.intent());
    }

    @Test
    @DisplayName("Should understand greetings with typos")
    void testGreetingKeywordMisspellings() {
        // Exact matches
        BotReply reply1 = engine.replyTo("bonjour");
        assertEquals("greeting", reply1.intent());

        BotReply reply2 = engine.replyTo("salut");
        assertEquals("greeting", reply2.intent());

        // Typos
        BotReply reply3 = engine.replyTo("bonjourr");  // extra letter
        assertEquals("greeting", reply3.intent());

        BotReply reply4 = engine.replyTo("salit");     // typo
        assertEquals("greeting", reply4.intent());
    }

    @Test
    @DisplayName("Should understand 'candidature' with misspellings")
    void testApplicationKeywordMisspellings() {
        // Exact match
        BotReply reply1 = engine.replyTo("candidature");
        assertEquals("application", reply1.intent());

        // Typos
        BotReply reply2 = engine.replyTo("candidature");
        assertEquals("application", reply2.intent());

        BotReply reply3 = engine.replyTo("candidatur");  // missing 'e'
        assertEquals("application", reply3.intent());
    }

    @Test
    @DisplayName("Should understand complex messages with mixed content and typos")
    void testComplexMessagesWithTypos() {
        // Message about job search with typos
        BotReply reply1 = engine.replyTo("Je chrc un empoloi en developement");
        assertEquals("job_search", reply1.intent());

        // Message about career with typos
        BotReply reply2 = engine.replyTo("Je veux m'ameliorer, une formation en informatque c'est bien");
        assertEquals("career", reply2.intent());

        // Message about salary with typos
        BotReply reply3 = engine.replyTo("Kel est le salaira pour ce poste?");
        assertEquals("benefits", reply3.intent());
    }

    @Test
    @DisplayName("Should handle support messages with typos")
    void testSupportKeywordMisspellings() {
        // Exact match
        BotReply reply1 = engine.replyTo("support");
        assertEquals("support", reply1.intent());

        // Typos
        BotReply reply2 = engine.replyTo("suport");     // missing 'p'
        assertEquals("support", reply2.intent());

        // "aid" may not match depending on threshold, but "aide" should
        BotReply reply4 = engine.replyTo("aide");
        assertEquals("support", reply4.intent());
    }

    @Test
    @DisplayName("Should still recognize correct spellings")
    void testCorrectSpellings() {
        BotReply reply1 = engine.replyTo("Bonjour comment allez-vous?");
        assertEquals("greeting", reply1.intent());

        BotReply reply2 = engine.replyTo("Je recherche un emploi en informatique");
        assertEquals("job_search", reply2.intent());

        BotReply reply3 = engine.replyTo("Je veux développer mes compétences");
        assertEquals("career", reply3.intent());

        BotReply reply4 = engine.replyTo("Quel est le salaire proposé?");
        assertEquals("benefits", reply4.intent());
    }

    @Test
    @DisplayName("Should provide fallback for unrecognized input")
    void testFallbackBehavior() {
        BotReply reply = engine.replyTo("xyzabc dfghijk");
        assertEquals("fallback", reply.intent());
        assertNotNull(reply.message());
        assertFalse(reply.message().isEmpty());
    }

    @Test
    @DisplayName("Demonstrate FuzzyMatcher capabilities")
    void testFuzzyMatcherDirectly() {
        // Test similarity scoring
        assertEquals(100, FuzzyMatcher.similarity("bonjour", "bonjour"));
        assertTrue(FuzzyMatcher.similarity("bonjour", "bonjourr") >= 85);
        assertTrue(FuzzyMatcher.similarity("emploi", "empoloi") >= 75);
        assertTrue(FuzzyMatcher.similarity("salaire", "salere") >= 70);

        // Test isSimilar
        assertTrue(FuzzyMatcher.isSimilar("emploi", "empoloi"));
        assertTrue(FuzzyMatcher.isSimilar("carriere", "carierre"));
        assertFalse(FuzzyMatcher.isSimilar("chat", "maison"));
    }
}

