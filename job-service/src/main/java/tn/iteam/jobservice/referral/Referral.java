package tn.iteam.jobservice.referral;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "referrals")
@EntityListeners(AuditingEntityListener.class)
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Email de l'employé qui coopte (extrait du JWT) */
    @Column(name = "referrer_email", nullable = false, length = 255)
    private String referrerEmail;

    @Column(name = "candidate_full_name", length = 200)
    private String candidateFullName;

    @Column(name = "candidate_email", length = 255)
    private String candidateEmail;

    @Column(name = "candidate_phone", length = 30)
    private String candidatePhone;

    @Column(name = "linked_in", length = 500)
    private String linkedIn;

    /** UUID de l'offre ciblée (pas de FK cross-service) */
    @Column(name = "target_job_id", length = 36)
    private String targetJobId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "referral_skills", joinColumns = @JoinColumn(name = "referral_id"))
    @Column(name = "skill", length = 100)
    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @Column(name = "cv_document_id", length = 80)
    private String cvDocumentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.DRAFT;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public enum Status { DRAFT, SUBMITTED, REVIEW, INTERVIEW, OFFER, HIRED, REJECTED }
}

