package com.talentconnect.candidatures.infrastructure.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CandidateAppliedKafkaProducer {

	private static final Logger log = LoggerFactory.getLogger(CandidateAppliedKafkaProducer.class);

	private final KafkaTemplate<String, CandidateAppliedPayload> kafkaTemplate;
	private final String topic;

	public CandidateAppliedKafkaProducer(KafkaTemplate<String, CandidateAppliedPayload> kafkaTemplate,
			@Value("${app.kafka.topics.candidate-applied:candidate.applied}") String topic) {
		this.kafkaTemplate = kafkaTemplate;
		this.topic = topic;
	}

	public void send(CandidateAppliedPayload payload) {
		try {
			kafkaTemplate.send(topic, String.valueOf(payload.candidatureId()), payload)
					.whenComplete((result, exception) -> {
						if (exception != null) {
							log.error("Failed to publish CandidateApplied for candidature {} (Kafka unavailable — event skipped)",
									payload.candidatureId(), exception);
						}
						else {
							log.info("Published CandidateApplied for candidature {}", payload.candidatureId());
						}
					});
		} catch (Exception ex) {
			// Kafka indisponible en dev local : on logue et on continue sans faire échouer la requête HTTP
			log.error("Kafka send threw synchronous exception for candidature {} — event skipped",
					payload.candidatureId(), ex);
		}
	}
}
