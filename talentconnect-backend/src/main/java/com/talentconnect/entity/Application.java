package com.talentconnect.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity @Table(name="applications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Application {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="job_id",nullable=false) private JobOffer job;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="employee_id") private User employee;
    @Column(name="candidate_name",length=200) private String candidateName;
    @Enumerated(EnumType.STRING) @Column(length=20) private Source source;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) @Builder.Default private Status status=Status.SUBMITTED;
    private int score;
    @Column(columnDefinition="TEXT") private String notes;
    @CreationTimestamp @Column(name="created_at",updatable=false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name="updated_at") private LocalDateTime updatedAt;
    @OneToMany(mappedBy="application",cascade=CascadeType.ALL,orphanRemoval=true)
    @Builder.Default private List<TimelineEntry> timeline=new ArrayList<>();
    @OneToMany(mappedBy="application",cascade=CascadeType.ALL,orphanRemoval=true)
    @Builder.Default private List<DocumentFile> documents=new ArrayList<>();
    public enum Source { INTERNAL, REFERRAL }
    public enum Status { SUBMITTED, REVIEW, INTERVIEW, OFFER, HIRED, REJECTED }
}
