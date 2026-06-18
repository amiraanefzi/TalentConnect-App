package com.talentconnect.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="timeline_entries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TimelineEntry {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="application_id",nullable=false) private Application application;
    @Column(length=200) private String title;
    @Column(columnDefinition="TEXT") private String description;
    @Column(length=150) private String author;
    private LocalDateTime timestamp;
}
