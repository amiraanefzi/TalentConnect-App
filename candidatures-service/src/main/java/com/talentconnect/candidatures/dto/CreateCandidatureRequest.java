package com.talentconnect.candidatures.dto;

import com.talentconnect.candidatures.domain.CandidatureType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCandidatureRequest(
		@NotNull(message = "L'identifiant de l'offre est obligatoire")
		@Positive(message = "L'identifiant de l'offre doit etre positif")
		Long offerId,

		@NotNull(message = "Le type de candidature est obligatoire")
		CandidatureType type,

		@Positive(message = "L'identifiant de recommandation doit etre positif")
		Long referralId) {
}
