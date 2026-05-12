package tn.iteam.jobservice.jobs.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import tn.iteam.jobservice.jobs.domain.EmploymentType;
import tn.iteam.jobservice.jobs.domain.ExperienceLevel;

public record JobOfferUpdateRequest(
        @NotBlank @Size(max = 140) String title,
        @NotBlank @Size(max = 140) String companyName,
        @NotBlank @Size(max = 140) String location,
        @NotNull EmploymentType employmentType,
        @NotNull ExperienceLevel experienceLevel,
        @NotNull Boolean remote,
        @NotBlank @Size(max = 50000) String description,
        @Min(0) Integer salaryMin,
        @Min(0) Integer salaryMax,
        @Pattern(regexp = "^[A-Z]{3}$", message = "must be a 3-letter ISO currency code (e.g. USD, EUR, TND)") String currency,
        @NotNull Boolean published
) {
}

