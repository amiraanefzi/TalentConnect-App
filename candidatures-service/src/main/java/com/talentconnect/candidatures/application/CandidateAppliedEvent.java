package com.talentconnect.candidatures.application;

import java.time.Instant;

import com.talentconnect.candidatures.domain.CandidatureType;

public record CandidateAppliedEvent(
		Long candidatureId,
		Long offerId,
		Long applicantUserId,
		CandidatureType type,
		Instant createdAt) {
}
