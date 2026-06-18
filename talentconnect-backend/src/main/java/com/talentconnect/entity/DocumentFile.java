package com.talentconnect.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DocumentFile {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="application_id",nullable=false) private Application application;
    @Column(name="file_name",length=255) private String fileName;
    @Column(name="mime_type",length=100) private String mimeType;
    private Long size;
    @Column(name="preview_url",length=500) private String previewUrl;
    @Column(name="download_url",length=500) private String downloadUrl;
    @Enumerated(EnumType.STRING) @Column(name="scan_status",length=20) @Builder.Default private ScanStatus scanStatus=ScanStatus.PENDING;
    @Column(name="uploaded_at") private LocalDateTime uploadedAt;
    public enum ScanStatus { PENDING, SAFE, FAILED }
}
