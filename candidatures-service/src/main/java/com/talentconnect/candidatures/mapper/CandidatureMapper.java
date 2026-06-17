package com.talentconnect.candidatures.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.talentconnect.candidatures.domain.Candidature;
import com.talentconnect.candidatures.domain.CandidatureStatusHistory;
import com.talentconnect.candidatures.dto.CandidatureDetailsResponse;
import com.talentconnect.candidatures.dto.CandidatureResponse;
import com.talentconnect.candidatures.dto.CandidatureStatusHistoryResponse;

@Component
public class CandidatureMapper {

	public CandidatureResponse toResponse(Candidature candidature) {
		return new CandidatureResponse(
				candidature.getId(),
				candidature.getOfferId(),
				candidature.getApplicantUserId(),
				candidature.getType(),
				candidature.getStatus(),
				candidature.getCreatedAt(),
				candidature.getUpdatedAt(),
				candidature.getCvFileId(),
				candidature.getReferralId());
	}

	public CandidatureStatusHistoryResponse toHistoryResponse(CandidatureStatusHistory history) {
		return new CandidatureStatusHistoryResponse(
				history.getId(),
				history.getFromStatus(),
				history.getToStatus(),
				history.getChangedAt(),
				history.getChangedBy(),
				history.getChangedByRole());
	}

	public CandidatureDetailsResponse toDetailsResponse(Candidature candidature, List<CandidatureStatusHistory> history) {
		return new CandidatureDetailsResponse(toResponse(candidature),
				history.stream().map(this::toHistoryResponse).toList());
	}
}
