package com.talentconnect.candidatures.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.talentconnect.candidatures.application.CandidatureService;
import com.talentconnect.candidatures.domain.CandidatureStatus;
import com.talentconnect.candidatures.domain.CandidatureType;
import com.talentconnect.candidatures.dto.*;
import com.talentconnect.candidatures.infrastructure.security.CurrentSecurity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/api/candidatures")
@Tag(
		name = "Candidatures",
		description = "API de gestion des candidatures. Authentification simulée via headers X-User-Id et X-Role."
)
public class CandidatureController {

	private final CandidatureService candidatureService;
	private final CurrentSecurity currentSecurity;

	public CandidatureController(CandidatureService candidatureService, CurrentSecurity currentSecurity) {
		this.candidatureService = candidatureService;
		this.currentSecurity = currentSecurity;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('EMPLOYEE')")
	@Operation(
			summary = "Créer une candidature",
			parameters = {
					@Parameter(
							name = "X-User-Id",
							in = ParameterIn.HEADER,
							required = true,
							description = "Identifiant de l'utilisateur connecté",
							example = "42"
					),
					@Parameter(
							name = "X-Role",
							in = ParameterIn.HEADER,
							required = true,
							description = "Rôle de l'utilisateur (EMPLOYEE)",
							example = "EMPLOYEE"
					)
			}
	)
	public CandidatureResponse create(@Valid @RequestBody CreateCandidatureRequest request) {
		return candidatureService.createCandidature(
				currentSecurity.userId(),
				request.offerId(),
				request.type(),
				request.referralId()
		);
	}

	@GetMapping("/me")
	@PreAuthorize("hasRole('EMPLOYEE')")
	@Operation(
			summary = "Lister mes candidatures",
			parameters = {
					@Parameter(name = "X-User-Id", in = ParameterIn.HEADER, required = true, example = "42"),
					@Parameter(name = "X-Role", in = ParameterIn.HEADER, required = true, example = "EMPLOYEE")
			}
	)
	public PageResponse<CandidatureResponse> listMine(
			@RequestParam(required = false) CandidatureStatus status,
			@RequestParam(required = false) CandidatureType type,
			@RequestParam(required = false) Long offerId,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

		return candidatureService.listMyCandidatures(
				currentSecurity.userId(), status, type, offerId, page, size
		);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('EMPLOYEE','RH')")
	@Operation(
			summary = "Consulter le détail d'une candidature",
			parameters = {
					@Parameter(name = "X-User-Id", in = ParameterIn.HEADER, required = true, example = "42"),
					@Parameter(name = "X-Role", in = ParameterIn.HEADER, required = true, example = "EMPLOYEE")
			}
	)
	public CandidatureDetailsResponse getDetails(@PathVariable Long id) {
		return candidatureService.getCandidatureDetails(
				id, currentSecurity.role(), currentSecurity.userId()
		);
	}

	@GetMapping
	@PreAuthorize("hasRole('RH')")
	@Operation(
			summary = "Lister toutes les candidatures côté RH",
			parameters = {
					@Parameter(name = "X-User-Id", in = ParameterIn.HEADER, required = true, example = "1"),
					@Parameter(name = "X-Role", in = ParameterIn.HEADER, required = true, example = "RH")
			}
	)
	public PageResponse<CandidatureResponse> listForRh(
			@RequestParam(required = false) CandidatureStatus status,
			@RequestParam(required = false) CandidatureType type,
			@RequestParam(required = false) Long offerId,
			@RequestParam(required = false) Long applicantUserId,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

		return candidatureService.listCandidaturesForRH(
				status, type, offerId, applicantUserId, page, size
		);
	}

	@PatchMapping("/{id}/status")
	@PreAuthorize("hasRole('RH')")
	@Operation(
			summary = "Changer le statut d'une candidature",
			parameters = {
					@Parameter(name = "X-User-Id", in = ParameterIn.HEADER, required = true, example = "1"),
					@Parameter(name = "X-Role", in = ParameterIn.HEADER, required = true, example = "RH")
			}
	)
	public CandidatureResponse changeStatus(
			@PathVariable Long id,
			@Valid @RequestBody ChangeStatusRequest request) {

		return candidatureService.changeStatus(
				id, request.newStatus(), currentSecurity.userId()
		);
	}


	/** DELETE /api/candidatures/{id} — retrait d'une candidature par son propriétaire */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasRole('EMPLOYEE')")
	@Operation(
			summary = "Retirer une candidature (propriétaire uniquement)",
			parameters = {
					@Parameter(name = "X-User-Id", in = ParameterIn.HEADER, required = true, example = "42"),
					@Parameter(name = "X-Role",    in = ParameterIn.HEADER, required = true, example = "EMPLOYEE")
			}
	)
	public void delete(@PathVariable Long id) {
		candidatureService.deleteCandidature(id, currentSecurity.userId());
	}

	@PatchMapping("/{id}/cv")
	@PreAuthorize("hasAnyRole('EMPLOYEE','RH')")
	@Operation(
			summary = "Attacher un CV (fileId) à une candidature",
			parameters = {
					@Parameter(name = "X-User-Id", in = ParameterIn.HEADER, required = true, example = "42"),
					@Parameter(name = "X-Role", in = ParameterIn.HEADER, required = true, example = "EMPLOYEE")
			}
	)
	public CandidatureResponse attachCv(
			@PathVariable Long id,
			@Valid @RequestBody AttachCvRequest request) {

		return candidatureService.attachCv(
				id, request.cvFileId(), currentSecurity.role(), currentSecurity.userId()
		);
	}
}