package com.talentconnect.dto;
import com.talentconnect.entity.Application;
import com.talentconnect.entity.DocumentFile;
import com.talentconnect.entity.TimelineEntry;
import java.time.LocalDateTime;
import java.util.List;
public record ApplicationDto(Long id,Long jobId,String jobTitle,Long employeeId,String candidateName,Application.Source source,Application.Status status,int score,String notes,LocalDateTime createdAt,LocalDateTime updatedAt,List<TimelineDto> timeline,List<DocumentDto> documents){
    public record TimelineDto(Long id,String title,String description,String author,LocalDateTime timestamp){
        public static TimelineDto from(TimelineEntry t){return new TimelineDto(t.getId(),t.getTitle(),t.getDescription(),t.getAuthor(),t.getTimestamp());}
    }
    public record DocumentDto(Long id,String fileName,String mimeType,Long size,String previewUrl,String downloadUrl,DocumentFile.ScanStatus scanStatus,LocalDateTime uploadedAt){
        public static DocumentDto from(DocumentFile d){return new DocumentDto(d.getId(),d.getFileName(),d.getMimeType(),d.getSize(),d.getPreviewUrl(),d.getDownloadUrl(),d.getScanStatus(),d.getUploadedAt());}
    }
    public static ApplicationDto from(Application a){return new ApplicationDto(a.getId(),a.getJob().getId(),a.getJob().getTitle(),a.getEmployee()!=null?a.getEmployee().getId():null,a.getCandidateName(),a.getSource(),a.getStatus(),a.getScore(),a.getNotes(),a.getCreatedAt(),a.getUpdatedAt(),a.getTimeline().stream().map(TimelineDto::from).toList(),a.getDocuments().stream().map(DocumentDto::from).toList());}
}
