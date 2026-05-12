package tn.iteam.jobservice.jobs.api.dto;

import tn.iteam.jobservice.jobs.domain.EmploymentType;
import tn.iteam.jobservice.jobs.domain.ExperienceLevel;

import java.time.Instant;
import java.util.UUID;

public record JobOfferResponse(
        UUID id,
        String title,
        String companyName,
        String location,
        EmploymentType employmentType,
        ExperienceLevel experienceLevel,
        boolean remote,
        String description,
        Integer salaryMin,
        Integer salaryMax,
        String currency,
        boolean published,
        Instant publishedAt,
        Instant createdAt,
        Instant updatedAt
) {
}

