package com.talentconnect.repository;
import com.talentconnect.entity.JobOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface JobOfferRepository extends JpaRepository<JobOffer,Long> {
    List<JobOffer> findByStatus(JobOffer.Status status);
    Page<JobOffer> findByStatus(JobOffer.Status status, Pageable pageable);
    @Query("SELECT j FROM JobOffer j WHERE (:q IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%',:q,'%'))) AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%',:location,'%'))) AND (:department IS NULL OR j.department=:department) AND (:status IS NULL OR j.status=:status)")
    Page<JobOffer> search(@Param("q") String q,@Param("location") String location,@Param("department") String department,@Param("status") JobOffer.Status status,Pageable pageable);
}
