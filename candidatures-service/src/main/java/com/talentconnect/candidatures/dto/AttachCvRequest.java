package com.talentconnect.candidatures.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AttachCvRequest(
		@NotBlank(message = "L'identifiant du fichier CV est obligatoire")
		@Size(max = 80, message = "L'identifiant du fichier CV ne doit pas depasser 80 caracteres")
		String cvFileId) {
}
