package com.talentconnect.repository;
import com.talentconnect.entity.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AuditEventRepository extends JpaRepository<AuditEvent,Long> {
    Page<AuditEvent> findAllByOrderByTimestampDesc(Pageable pageable);
}
