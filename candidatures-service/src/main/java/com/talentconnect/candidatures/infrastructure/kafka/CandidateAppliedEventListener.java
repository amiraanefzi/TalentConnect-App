package com.talentconnect.candidatures.infrastructure.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.talentconnect.candidatures.application.CandidateAppliedEvent;

@Component
public class CandidateAppliedEventListener {

	private static final Logger log = LoggerFactory.getLogger(CandidateAppliedEventListener.class);

	private final CandidateAppliedKafkaProducer producer;

	public CandidateAppliedEventListener(CandidateAppliedKafkaProducer producer) {
		this.producer = producer;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onCandidateApplied(CandidateAppliedEvent event) {
		try {
			producer.send(new CandidateAppliedPayload(
					event.candidatureId(),
					event.offerId(),
					event.applicantUserId(),
					event.type(),
					event.createdAt()));
		} catch (Exception ex) {
			// Ne pas faire échouer la réponse HTTP si Kafka est indisponible (dev local sans broker)
			log.error("Unexpected error publishing CandidateApplied event for candidature {} — ignored",
					event.candidatureId(), ex);
		}
	}
}
