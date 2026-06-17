package com.talentconnect.candidatures.dto;

import java.time.Instant;

import com.talentconnect.candidatures.domain.CandidatureStatus;

public record CandidatureStatusHistoryResponse(
		Long id,
		CandidatureStatus fromStatus,
		CandidatureStatus toStatus,
		Instant changedAt,
		Long changedBy,
		String changedByRole) {
}
