package com.talentconnect.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
@Entity @Table(name="notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private User user;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=15) private NotifType type;
    @Column(nullable=false,length=200) private String title;
    @Column(columnDefinition="TEXT") private String message;
    @Column(nullable=false) @Builder.Default private boolean read=false;
    @Column(name="deep_link",length=500) private String deepLink;
    @CreationTimestamp @Column(name="created_at",updatable=false) private LocalDateTime createdAt;
    public enum NotifType { SUCCESS, INFO, WARNING, ERROR }
}
