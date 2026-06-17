package com.talentconnect.candidatures.dto;

import java.util.List;

public record CandidatureDetailsResponse(
		CandidatureResponse candidature,
		List<CandidatureStatusHistoryResponse> history) {
}
