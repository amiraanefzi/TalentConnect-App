package tn.iteam.jobservice.jobs.api;

import tn.iteam.jobservice.jobs.api.dto.JobOfferCreateRequest;
import tn.iteam.jobservice.jobs.api.dto.JobOfferResponse;
import tn.iteam.jobservice.jobs.domain.JobOffer;

public final class JobOfferMapper {
    private JobOfferMapper() {
    }

    public static JobOffer toEntity(JobOfferCreateRequest req) {
        return JobOffer.builder()
                .title(req.title())
                .companyName(req.companyName())
                .location(req.location())
                .employmentType(req.employmentType())
                .experienceLevel(req.experienceLevel())
                .remote(Boolean.TRUE.equals(req.remote()))
                .description(req.description())
                .salaryMin(req.salaryMin())
                .salaryMax(req.salaryMax())
                .currency(req.currency())
                .published(Boolean.TRUE.equals(req.published()))
                .build();
    }

    public static JobOfferResponse toResponse(JobOffer e) {
        return new JobOfferResponse(
                e.getId(),
                e.getTitle(),
                e.getCompanyName(),
                e.getLocation(),
                e.getEmploymentType(),
                e.getExperienceLevel(),
                e.isRemote(),
                e.getDescription(),
                e.getSalaryMin(),
                e.getSalaryMax(),
                e.getCurrency(),
                e.isPublished(),
                e.getPublishedAt(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }
}

