package com.talentconnect.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="audit_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditEvent {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=255) private String actor;
    @Column(name="actor_role",length=20) private String actorRole;
    @Column(nullable=false,length=100) private String action;
    @Enumerated(EnumType.STRING) @Column(name="entity_type",length=20) private EntityType entityType;
    @Column(name="entity_id",length=100) private String entityId;
    @Column(columnDefinition="TEXT") private String details;
    @Column(nullable=false) private LocalDateTime timestamp;
    @PrePersist void prePersist(){ if(this.timestamp==null) this.timestamp=LocalDateTime.now(); }
    public enum EntityType { JOB, APPLICATION, REFERRAL, DOCUMENT }
}
