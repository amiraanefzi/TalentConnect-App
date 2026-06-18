package com.talentconnect.dto;
import com.talentconnect.entity.JobOffer;
import java.time.LocalDateTime;
import java.util.List;
public record JobOfferDto(Long id,String title,String department,String location,String description,JobOffer.EmploymentType employmentType,JobOffer.Seniority seniority,JobOffer.Status status,List<String> requirements,List<String> tags,LocalDateTime publishedAt,LocalDateTime closingAt,String hiringManager,Integer recommendedScore,LocalDateTime createdAt,LocalDateTime updatedAt){
    public static JobOfferDto from(JobOffer j){return new JobOfferDto(j.getId(),j.getTitle(),j.getDepartment(),j.getLocation(),j.getDescription(),j.getEmploymentType(),j.getSeniority(),j.getStatus(),j.getRequirements(),j.getTags(),j.getPublishedAt(),j.getClosingAt(),j.getHiringManager(),j.getRecommendedScore(),j.getCreatedAt(),j.getUpdatedAt());}
}
