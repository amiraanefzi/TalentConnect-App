package com.talentconnect.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity @Table(name="job_offers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JobOffer {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=200) private String title;
    @Column(length=100) private String department;
    @Column(length=150) private String location;
    @Column(columnDefinition="TEXT") private String description;
    @Enumerated(EnumType.STRING) @Column(name="employment_type",length=20) private EmploymentType employmentType;
    @Enumerated(EnumType.STRING) @Column(length=20) private Seniority seniority;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=10) @Builder.Default private Status status=Status.DRAFT;
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="job_requirements",joinColumns=@JoinColumn(name="job_id"))
    @Column(name="requirement",length=200) @Builder.Default private List<String> requirements=new ArrayList<>();
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="job_tags",joinColumns=@JoinColumn(name="job_id"))
    @Column(name="tag",length=50) @Builder.Default private List<String> tags=new ArrayList<>();
    @Column(name="published_at") private LocalDateTime publishedAt;
    @Column(name="closing_at") private LocalDateTime closingAt;
    @Column(name="hiring_manager",length=150) private String hiringManager;
    @Column(name="recommended_score") private Integer recommendedScore;
    @CreationTimestamp @Column(name="created_at",updatable=false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name="updated_at") private LocalDateTime updatedAt;
    public enum EmploymentType { CDI, CDD, STAGE, FREELANCE }
    public enum Seniority { JUNIOR, CONFIRME, SENIOR, LEAD }
    public enum Status { DRAFT, OPEN, CLOSED }
}
