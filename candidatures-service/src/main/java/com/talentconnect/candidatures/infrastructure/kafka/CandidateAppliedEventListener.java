package com.talentconnect.candidatures.infrastructure.kafka;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.talentconnect.candidatures.application.CandidateAppliedEvent;

@Component
public class CandidateAppliedEventListener {

	private final CandidateAppliedKafkaProducer producer;

	public CandidateAppliedEventListener(CandidateAppliedKafkaProducer producer) {
		this.producer = producer;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onCandidateApplied(CandidateAppliedEvent event) {
		producer.send(new CandidateAppliedPayload(
				event.candidatureId(),
				event.offerId(),
				event.applicantUserId(),
				event.type(),
				event.createdAt()));
	}
}
