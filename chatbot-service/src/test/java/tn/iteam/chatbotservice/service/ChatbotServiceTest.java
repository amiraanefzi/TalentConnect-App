package tn.iteam.chatbotservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.iteam.chatbotservice.domain.ChatConversation;
import tn.iteam.chatbotservice.domain.ChatSender;
import tn.iteam.chatbotservice.dto.ChatRequest;
import tn.iteam.chatbotservice.dto.ChatResponse;
import tn.iteam.chatbotservice.engine.ChatbotEngine;
import tn.iteam.chatbotservice.engine.JobSearchHandler;
import tn.iteam.chatbotservice.repository.ChatConversationRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    @Mock
    private ChatConversationRepository conversationRepository;

    @Mock
    private JobSearchHandler jobSearchHandler;

    private final ChatbotEngine chatbotEngine = new ChatbotEngine();

    private ChatbotService chatbotService;

    @BeforeEach
    void setUp() {
        // jobSearchHandler retourne null par défaut → délègue au ChatbotEngine statique
        when(jobSearchHandler.handle(anyString())).thenReturn(null);
        chatbotService = new ChatbotService(chatbotEngine, jobSearchHandler, conversationRepository);
    }

    @Test
    void replyStoresUserAndBotMessages() {
        when(conversationRepository.save(any(ChatConversation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ChatResponse response = chatbotService.reply(new ChatRequest("user-1", "bonjour"));

        ArgumentCaptor<ChatConversation> captor = ArgumentCaptor.forClass(ChatConversation.class);
        verify(conversationRepository, org.mockito.Mockito.times(2)).save(captor.capture());

        assertThat(response.intent()).isEqualTo("greeting");
        assertThat(response.response()).isNotBlank();
        assertThat(captor.getAllValues())
                .extracting(ChatConversation::getSender)
                .containsExactly(ChatSender.USER, ChatSender.BOT);
    }

    @Test
    void clearHistoryDeletesByUserId() {
        when(conversationRepository.deleteByUserId("user-1")).thenReturn(4L);

        long deleted = chatbotService.clearHistory("user-1");

        assertThat(deleted).isEqualTo(4L);
        verify(conversationRepository).deleteByUserId("user-1");
    }
}
