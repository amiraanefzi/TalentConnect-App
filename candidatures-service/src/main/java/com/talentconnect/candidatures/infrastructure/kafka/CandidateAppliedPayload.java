package com.talentconnect.candidatures.infrastructure.kafka;

import java.time.Instant;

import com.talentconnect.candidatures.domain.CandidatureType;

public record CandidateAppliedPayload(
		Long candidatureId,
		Long offerId,
		Long applicantUserId,
		CandidatureType type,
		Instant createdAt) {
}
