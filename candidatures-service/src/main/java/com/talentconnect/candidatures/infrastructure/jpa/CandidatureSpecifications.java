package com.talentconnect.candidatures.infrastructure.jpa;

import org.springframework.data.jpa.domain.Specification;

import com.talentconnect.candidatures.domain.Candidature;
import com.talentconnect.candidatures.domain.CandidatureStatus;
import com.talentconnect.candidatures.domain.CandidatureType;

public final class CandidatureSpecifications {

	private CandidatureSpecifications() {
	}

	public static Specification<Candidature> applicantUserId(Long applicantUserId) {
		return (root, query, criteriaBuilder) -> applicantUserId == null
				? criteriaBuilder.conjunction()
				: criteriaBuilder.equal(root.get("applicantUserId"), applicantUserId);
	}

	public static Specification<Candidature> status(CandidatureStatus status) {
		return (root, query, criteriaBuilder) -> status == null
				? criteriaBuilder.conjunction()
				: criteriaBuilder.equal(root.get("status"), status);
	}

	public static Specification<Candidature> type(CandidatureType type) {
		return (root, query, criteriaBuilder) -> type == null
				? criteriaBuilder.conjunction()
				: criteriaBuilder.equal(root.get("type"), type);
	}

	public static Specification<Candidature> offerId(Long offerId) {
		return (root, query, criteriaBuilder) -> offerId == null
				? criteriaBuilder.conjunction()
				: criteriaBuilder.equal(root.get("offerId"), offerId);
	}
}
