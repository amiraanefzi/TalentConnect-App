package com.talentconnect.candidatures.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "candidatures", uniqueConstraints = {
		@UniqueConstraint(name = "uk_candidature_applicant_offer", columnNames = { "applicant_user_id", "offer_id" })
})
public class Candidature {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "offer_id", nullable = false)
	private Long offerId;

	@Column(name = "applicant_user_id", nullable = false)
	private Long applicantUserId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private CandidatureType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private CandidatureStatus status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Column(name = "cv_file_id", length = 80)
	private String cvFileId;

	@Column(name = "referral_id")
	private Long referralId;

	protected Candidature() {
	}

	public Candidature(Long offerId, Long applicantUserId, CandidatureType type, Long referralId) {
		this.offerId = offerId;
		this.applicantUserId = applicantUserId;
		this.type = type;
		this.referralId = referralId;
		this.status = CandidatureStatus.SOUMISE;
	}

	@PrePersist
	void prePersist() {
		Instant now = Instant.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		this.updatedAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public Long getOfferId() {
		return offerId;
	}

	public Long getApplicantUserId() {
		return applicantUserId;
	}

	public CandidatureType getType() {
		return type;
	}

	public CandidatureStatus getStatus() {
		return status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public String getCvFileId() {
		return cvFileId;
	}

	public Long getReferralId() {
		return referralId;
	}

	public void changeStatus(CandidatureStatus newStatus) {
		this.status = newStatus;
	}

	public void attachCv(String cvFileId) {
		this.cvFileId = cvFileId;
	}
}
