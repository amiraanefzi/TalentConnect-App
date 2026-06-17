package com.talentconnect.candidatures.infrastructure.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talentconnect.candidatures.domain.CandidatureStatusHistory;

public interface CandidatureStatusHistoryRepository extends JpaRepository<CandidatureStatusHistory, Long> {

	List<CandidatureStatusHistory> findByCandidatureIdOrderByChangedAtAsc(Long candidatureId);
}
