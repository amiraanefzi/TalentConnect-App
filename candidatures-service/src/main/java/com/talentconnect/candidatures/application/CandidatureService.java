package com.talentconnect.candidatures.application;

import static com.talentconnect.candidatures.infrastructure.jpa.CandidatureSpecifications.applicantUserId;
import static com.talentconnect.candidatures.infrastructure.jpa.CandidatureSpecifications.offerId;
import static com.talentconnect.candidatures.infrastructure.jpa.CandidatureSpecifications.status;
import static com.talentconnect.candidatures.infrastructure.jpa.CandidatureSpecifications.type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talentconnect.candidatures.domain.Candidature;
import com.talentconnect.candidatures.domain.CandidatureStatus;
import com.talentconnect.candidatures.domain.CandidatureStatusHistory;
import com.talentconnect.candidatures.domain.CandidatureType;
import com.talentconnect.candidatures.dto.CandidatureDetailsResponse;
import com.talentconnect.candidatures.dto.CandidatureResponse;
import com.talentconnect.candidatures.dto.PageResponse;
import com.talentconnect.candidatures.exception.DuplicateCandidatureException;
import com.talentconnect.candidatures.exception.ForbiddenException;
import com.talentconnect.candidatures.exception.ResourceNotFoundException;
import com.talentconnect.candidatures.infrastructure.jpa.CandidatureRepository;
import com.talentconnect.candidatures.infrastructure.jpa.CandidatureStatusHistoryRepository;
import com.talentconnect.candidatures.mapper.CandidatureMapper;

@Service
public class CandidatureService {

	private static final Logger log = LoggerFactory.getLogger(CandidatureService.class);
	private static final String ROLE_RH = "ROLE_RH";
	private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";

	private final CandidatureRepository candidatureRepository;
	private final CandidatureStatusHistoryRepository historyRepository;
	private final CandidatureMapper mapper;
	private final ApplicationEventPublisher eventPublisher;
	private final FileServiceGateway fileServiceGateway;
	private final NotificationService notificationService;

	public CandidatureService(CandidatureRepository candidatureRepository,
			CandidatureStatusHistoryRepository historyRepository,
			CandidatureMapper mapper,
			ApplicationEventPublisher eventPublisher,
			FileServiceGateway fileServiceGateway,
			NotificationService notificationService) {
		this.candidatureRepository = candidatureRepository;
		this.historyRepository = historyRepository;
		this.mapper = mapper;
		this.eventPublisher = eventPublisher;
		this.fileServiceGateway = fileServiceGateway;
		this.notificationService = notificationService;
	}

	@Transactional
	public CandidatureResponse createCandidature(Long applicantUserId, Long offerId, CandidatureType type,
			Long referralId) {
		if (candidatureRepository.existsByApplicantUserIdAndOfferId(applicantUserId, offerId)) {
			throw new DuplicateCandidatureException("Une candidature existe deja pour cette offre");
		}

		Candidature candidature = candidatureRepository.save(new Candidature(offerId, applicantUserId, type, referralId));
		eventPublisher.publishEvent(new CandidateAppliedEvent(candidature.getId(), candidature.getOfferId(),
				candidature.getApplicantUserId(), candidature.getType(), candidature.getCreatedAt()));
		log.info("Candidature {} created for applicant {} and offer {}", candidature.getId(), applicantUserId, offerId);

		// 🔔 Notification broadcast RH : nouvelle candidature à traiter
		String rhTitle   = "Nouvelle candidature reçue";
		String rhMessage = "L'utilisateur #" + applicantUserId + " a soumis une candidature "
				+ type.name() + " pour l'offre #" + offerId
				+ " (candidature #" + candidature.getId() + ")";
		String rhLink    = "/rh/candidatures/" + candidature.getId();
		notificationService.pushToRh(
				com.talentconnect.candidatures.domain.Notification.NotifType.INFO,
				rhTitle, rhMessage, rhLink);

		return mapper.toResponse(candidature);
	}

	@Transactional(readOnly = true)
	public PageResponse<CandidatureResponse> listMyCandidatures(Long requesterUserId, CandidatureStatus filterStatus,
			CandidatureType filterType, Long filterOfferId, int page, int size) {
		Specification<Candidature> specification = applicantUserId(requesterUserId)
				.and(status(filterStatus))
				.and(type(filterType))
				.and(offerId(filterOfferId));
		return PageResponse.from(candidatureRepository.findAll(specification, pageable(page, size)).map(mapper::toResponse));
	}

	@Transactional(readOnly = true)
	public PageResponse<CandidatureResponse> listCandidaturesForRH(CandidatureStatus filterStatus,
			CandidatureType filterType, Long filterOfferId, Long filterApplicantUserId, int page, int size) {
		Specification<Candidature> specification = applicantUserId(filterApplicantUserId)
				.and(status(filterStatus))
				.and(type(filterType))
				.and(offerId(filterOfferId));
		return PageResponse.from(candidatureRepository.findAll(specification, pageable(page, size)).map(mapper::toResponse));
	}

	@Transactional(readOnly = true)
	public CandidatureDetailsResponse getCandidatureDetails(Long id, String requesterRole, Long requesterUserId) {
		Candidature candidature = findCandidature(id);
		assertCanRead(candidature, requesterRole, requesterUserId);
		return mapper.toDetailsResponse(candidature, historyRepository.findByCandidatureIdOrderByChangedAtAsc(id));
	}

	@Transactional
	public CandidatureResponse changeStatus(Long id, CandidatureStatus newStatus, Long rhUserId) {
		Candidature candidature = findCandidature(id);
		CandidatureStatus oldStatus = candidature.getStatus();
		if (oldStatus == newStatus) {
			return mapper.toResponse(candidature);
		}

		candidature.changeStatus(newStatus);
		historyRepository.save(new CandidatureStatusHistory(id, oldStatus, newStatus, rhUserId, ROLE_RH));
		log.info("Candidature {} status changed from {} to {} by RH {}", id, oldStatus, newStatus, rhUserId);

		// 🔔 Notification au candidat
		String title   = "Statut de votre candidature mis à jour";
		String message = "Votre candidature #" + id + " est maintenant : " + newStatus.name();
		String link    = "/candidatures/" + id;
		notificationService.push(candidature.getApplicantUserId(),
				com.talentconnect.candidatures.domain.Notification.NotifType.INFO,
				title, message, link);

		return mapper.toResponse(candidature);
	}

	@Transactional
	public void deleteCandidature(Long id, Long requesterUserId) {
		Candidature candidature = findCandidature(id);
		if (!candidature.getApplicantUserId().equals(requesterUserId)) {
			throw new ForbiddenException("Seul le proprietaire peut retirer sa candidature");
		}
		candidatureRepository.deleteById(id);
		log.info("Candidature {} deleted by applicant {}", id, requesterUserId);
	}

	@Transactional
	public CandidatureResponse attachCv(Long id, String cvFileId, String requesterRole, Long requesterUserId) {
		Candidature candidature = findCandidature(id);
		assertCanAttachCv(candidature, requesterRole, requesterUserId);
		fileServiceGateway.assertFileExists(cvFileId, requesterUserId, requesterRole);
		candidature.attachCv(cvFileId);
		log.info("CV {} attached to candidature {} by {}", cvFileId, id, requesterUserId);
		return mapper.toResponse(candidature);
	}

	private Candidature findCandidature(Long id) {
		return candidatureRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable: " + id));
	}

	private void assertCanRead(Candidature candidature, String requesterRole, Long requesterUserId) {
		if (ROLE_RH.equals(requesterRole)) {
			return;
		}
		if (ROLE_EMPLOYEE.equals(requesterRole) && candidature.getApplicantUserId().equals(requesterUserId)) {
			return;
		}
		throw new ForbiddenException("Acces interdit a cette candidature");
	}

	private void assertCanAttachCv(Candidature candidature, String requesterRole, Long requesterUserId) {
		if (ROLE_RH.equals(requesterRole)) {
			return;
		}
		if (ROLE_EMPLOYEE.equals(requesterRole) && candidature.getApplicantUserId().equals(requesterUserId)) {
			return;
		}
		throw new ForbiddenException("Seul le RH ou le proprietaire peut attacher un CV");
	}

	private Pageable pageable(int page, int size) {
		int safePage = Math.max(page, 0);
		int safeSize = Math.clamp(size, 1, 100);
		return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
	}
}
