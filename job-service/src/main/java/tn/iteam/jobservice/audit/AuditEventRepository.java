package tn.iteam.jobservice.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    Page<AuditEvent> findAllByOrderByTimestampDesc(Pageable pageable);

    @Query("SELECT COUNT(a) FROM AuditEvent a WHERE a.timestamp >= :startOfDay")
    long countSince(@Param("startOfDay") Instant startOfDay);
}

