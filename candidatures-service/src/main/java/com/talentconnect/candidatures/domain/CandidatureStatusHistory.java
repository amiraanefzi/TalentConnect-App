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
import jakarta.persistence.Table;

@Entity
@Table(name = "candidature_status_history")
public class CandidatureStatusHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "candidature_id", nullable = false)
	private Long candidatureId;

	@Enumerated(EnumType.STRING)
	@Column(name = "from_status", nullable = false, length = 30)
	private CandidatureStatus fromStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "to_status", nullable = false, length = 30)
	private CandidatureStatus toStatus;

	@Column(name = "changed_at", nullable = false)
	private Instant changedAt;

	@Column(name = "changed_by", nullable = false)
	private Long changedBy;

	@Column(name = "changed_by_role", nullable = false, length = 50)
	private String changedByRole;

	protected CandidatureStatusHistory() {
	}

	public CandidatureStatusHistory(Long candidatureId, CandidatureStatus fromStatus, CandidatureStatus toStatus,
			Long changedBy, String changedByRole) {
		this.candidatureId = candidatureId;
		this.fromStatus = fromStatus;
		this.toStatus = toStatus;
		this.changedBy = changedBy;
		this.changedByRole = changedByRole;
	}

	@PrePersist
	void prePersist() {
		this.changedAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public Long getCandidatureId() {
		return candidatureId;
	}

	public CandidatureStatus getFromStatus() {
		return fromStatus;
	}

	public CandidatureStatus getToStatus() {
		return toStatus;
	}

	public Instant getChangedAt() {
		return changedAt;
	}

	public Long getChangedBy() {
		return changedBy;
	}

	public String getChangedByRole() {
		return changedByRole;
	}
}
