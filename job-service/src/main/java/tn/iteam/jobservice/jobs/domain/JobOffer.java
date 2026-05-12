package tn.iteam.jobservice.jobs.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "job_offers",
        indexes = {
                @Index(name = "idx_job_offer_published", columnList = "published"),
                @Index(name = "idx_job_offer_created_at", columnList = "created_at")
        }
)
@EntityListeners(AuditingEntityListener.class)
public class JobOffer {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, length = 140)
    private String title;

    @Column(name = "company_name", nullable = false, length = 140)
    private String companyName;

    @Column(nullable = false, length = 140)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false, length = 32)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false, length = 32)
    private ExperienceLevel experienceLevel;

    @Column(nullable = false)
    private boolean remote;

    @Lob
    @Column(nullable = false)
    private String description;

    private Integer salaryMin;
    private Integer salaryMax;

    @Column(length = 8)
    private String currency;

    @Column(nullable = false)
    private boolean published;

    @Column(name = "published_at")
    private Instant publishedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
