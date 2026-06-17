package com.talentconnect.candidatures.dto;

import java.time.Instant;

import com.talentconnect.candidatures.domain.CandidatureStatus;
import com.talentconnect.candidatures.domain.CandidatureType;

public record CandidatureResponse(
		Long id,
		Long offerId,
		Long applicantUserId,
		CandidatureType type,
		CandidatureStatus status,
		Instant createdAt,
		Instant updatedAt,
		String cvFileId,
		Long referralId) {
}
