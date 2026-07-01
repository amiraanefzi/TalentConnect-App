package com.talentconnect.candidatures.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.talentconnect.candidatures.domain.Candidature;

public interface CandidatureRepository
		extends JpaRepository<Candidature, Long>, JpaSpecificationExecutor<Candidature> {

	boolean existsByApplicantUserIdAndOfferId(Long applicantUserId, Long offerId);

	long countByStatus(com.talentconnect.candidatures.domain.CandidatureStatus status);

	long countByType(com.talentconnect.candidatures.domain.CandidatureType type);
}
