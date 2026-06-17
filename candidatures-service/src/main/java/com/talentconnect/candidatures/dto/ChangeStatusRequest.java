package com.talentconnect.candidatures.dto;

import com.talentconnect.candidatures.domain.CandidatureStatus;

import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(
		@NotNull(message = "Le nouveau statut est obligatoire")
		CandidatureStatus newStatus) {
}
