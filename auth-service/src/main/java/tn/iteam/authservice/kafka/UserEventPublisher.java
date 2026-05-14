package tn.iteam.authservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tn.iteam.authservice.user.User;

import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class UserEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(UserEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String userCreatedTopic;

    public UserEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topics.user-created}") String userCreatedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.userCreatedTopic = userCreatedTopic;
    }

    public void publishUserCreated(User user) {
        UserCreatedEvent event = new UserCreatedEvent(
                user.getId(),
                user.getEmail(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                user.getCreatedAt()
        );
        try {
            kafkaTemplate.send(userCreatedTopic, String.valueOf(user.getId()), event);
        } catch (Exception ex) {
            log.warn("Failed to publish Kafka event user.created for userId={}", user.getId(), ex);
        }
    }
}
