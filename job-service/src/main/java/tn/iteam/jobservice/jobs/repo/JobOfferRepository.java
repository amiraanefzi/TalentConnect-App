package tn.iteam.jobservice.jobs.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tn.iteam.jobservice.jobs.domain.JobOffer;

import java.util.UUID;

public interface JobOfferRepository extends JpaRepository<JobOffer, UUID>, JpaSpecificationExecutor<JobOffer> {

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(j) FROM JobOffer j WHERE j.published = true")
    long countPublished();
}

