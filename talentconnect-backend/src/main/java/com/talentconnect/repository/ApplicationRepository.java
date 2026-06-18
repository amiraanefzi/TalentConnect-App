package com.talentconnect.repository;
import com.talentconnect.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ApplicationRepository extends JpaRepository<Application,Long> {
    Page<Application> findByEmployeeId(Long employeeId, Pageable pageable);
    Page<Application> findByJobId(Long jobId, Pageable pageable);
    boolean existsByEmployeeIdAndJobId(Long employeeId, Long jobId);
    long countByStatus(Application.Status status);
    long countBySource(Application.Source source);
}
