package com.talentconnect.candidatures.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import com.talentconnect.candidatures.domain.Candidature;
import com.talentconnect.candidatures.domain.CandidatureStatus;
import com.talentconnect.candidatures.domain.CandidatureStatusHistory;
import com.talentconnect.candidatures.domain.CandidatureType;
import com.talentconnect.candidatures.exception.DuplicateCandidatureException;
import com.talentconnect.candidatures.exception.ForbiddenException;
import com.talentconnect.candidatures.infrastructure.jpa.CandidatureRepository;
import com.talentconnect.candidatures.infrastructure.jpa.CandidatureStatusHistoryRepository;
import com.talentconnect.candidatures.mapper.CandidatureMapper;

class CandidatureServiceTest {

	private CandidatureRepository candidatureRepository;
	private CandidatureStatusHistoryRepository historyRepository;
	private ApplicationEventPublisher eventPublisher;
	private FileServiceGateway fileServiceGateway;
	private CandidatureService service;

	@BeforeEach
	void setUp() {
		candidatureRepository = org.mockito.Mockito.mock(CandidatureRepository.class);
		historyRepository = org.mockito.Mockito.mock(CandidatureStatusHistoryRepository.class);
		eventPublisher = org.mockito.Mockito.mock(ApplicationEventPublisher.class);
		fileServiceGateway = org.mockito.Mockito.mock(FileServiceGateway.class);
		service = new CandidatureService(candidatureRepository, historyRepository, new CandidatureMapper(),
					eventPublisher, fileServiceGateway);
	}

	@Test
	void createCandidaturePersistsAndPublishesEvent() {
		when(candidatureRepository.existsByApplicantUserIdAndOfferId(10L, 20L)).thenReturn(false);
		when(candidatureRepository.save(any(Candidature.class))).thenAnswer(invocation -> {
			Candidature candidature = invocation.getArgument(0);
			ReflectionTestUtils.setField(candidature, "id", 1L);
			ReflectionTestUtils.setField(candidature, "createdAt", Instant.parse("2026-05-08T20:00:00Z"));
			ReflectionTestUtils.setField(candidature, "updatedAt", Instant.parse("2026-05-08T20:00:00Z"));
			return candidature;
		});

		var response = service.createCandidature(10L, 20L, CandidatureType.INTERNE, null);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.status()).isEqualTo(CandidatureStatus.SOUMISE);
		ArgumentCaptor<CandidateAppliedEvent> eventCaptor = ArgumentCaptor.forClass(CandidateAppliedEvent.class);
		verify(eventPublisher).publishEvent(eventCaptor.capture());
		assertThat(eventCaptor.getValue().candidatureId()).isEqualTo(1L);
	}

	@Test
	void createCandidatureRejectsDuplicateApplication() {
		when(candidatureRepository.existsByApplicantUserIdAndOfferId(10L, 20L)).thenReturn(true);

		assertThatThrownBy(() -> service.createCandidature(10L, 20L, CandidatureType.INTERNE, null))
				.isInstanceOf(DuplicateCandidatureException.class);

		verify(candidatureRepository, never()).save(any());
	}

	@Test
	void changeStatusStoresHistory() {
		Candidature candidature = persistedCandidature(1L, 20L, 10L);
		when(candidatureRepository.findById(1L)).thenReturn(Optional.of(candidature));

		var response = service.changeStatus(1L, CandidatureStatus.ENTRETIEN, 99L);

		assertThat(response.status()).isEqualTo(CandidatureStatus.ENTRETIEN);
		ArgumentCaptor<CandidatureStatusHistory> historyCaptor =
				ArgumentCaptor.forClass(CandidatureStatusHistory.class);
		verify(historyRepository).save(historyCaptor.capture());
		assertThat(historyCaptor.getValue().getFromStatus()).isEqualTo(CandidatureStatus.SOUMISE);
		assertThat(historyCaptor.getValue().getToStatus()).isEqualTo(CandidatureStatus.ENTRETIEN);
		assertThat(historyCaptor.getValue().getChangedBy()).isEqualTo(99L);
		assertThat(historyCaptor.getValue().getChangedByRole()).isEqualTo("ROLE_RH");
	}

	@Test
	void employeeCannotReadAnotherApplicantCandidature() {
		Candidature candidature = persistedCandidature(1L, 20L, 10L);
		when(candidatureRepository.findById(1L)).thenReturn(Optional.of(candidature));

		assertThatThrownBy(() -> service.getCandidatureDetails(1L, "ROLE_EMPLOYEE", 11L))
				.isInstanceOf(ForbiddenException.class);
	}

	@Test
	void attachCvChecksFileWithFileService() {
		Candidature candidature = persistedCandidature(1L, 20L, 10L);
		when(candidatureRepository.findById(1L)).thenReturn(Optional.of(candidature));

		var response = service.attachCv(1L, "file-123", "ROLE_EMPLOYEE", 10L);

		assertThat(response.cvFileId()).isEqualTo("file-123");
		verify(fileServiceGateway).assertFileExists(eq("file-123"), eq(10L), eq("ROLE_EMPLOYEE"));
	}

	private Candidature persistedCandidature(Long id, Long offerId, Long applicantUserId) {
		Candidature candidature = new Candidature(offerId, applicantUserId, CandidatureType.INTERNE, null);
		ReflectionTestUtils.setField(candidature, "id", id);
		ReflectionTestUtils.setField(candidature, "createdAt", Instant.parse("2026-05-08T20:00:00Z"));
		ReflectionTestUtils.setField(candidature, "updatedAt", Instant.parse("2026-05-08T20:00:00Z"));
		return candidature;
	}
}
