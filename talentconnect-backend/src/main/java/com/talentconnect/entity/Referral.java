package com.talentconnect.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity @Table(name="referrals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Referral {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="referrer_employee_id",nullable=false) private User referrerEmployee;
    @Column(name="candidate_full_name",length=200) private String candidateFullName;
    @Column(name="candidate_email",length=255) private String candidateEmail;
    @Column(name="candidate_phone",length=30) private String candidatePhone;
    @Column(name="linked_in",length=500) private String linkedIn;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="target_job_id") private JobOffer targetJob;
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="referral_skills",joinColumns=@JoinColumn(name="referral_id"))
    @Column(name="skill",length=100) @Builder.Default private List<String> skills=new ArrayList<>();
    @Column(name="cv_document_id",length=80) private String cvDocumentId;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) @Builder.Default private Status status=Status.DRAFT;
    @CreationTimestamp @Column(name="created_at",updatable=false) private LocalDateTime createdAt;
    public enum Status { SUBMITTED, REVIEW, INTERVIEW, OFFER, HIRED, REJECTED, DRAFT }
}
