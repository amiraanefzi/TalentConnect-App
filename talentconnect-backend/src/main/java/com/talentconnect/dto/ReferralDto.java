package com.talentconnect.dto;
import com.talentconnect.entity.Referral;
import java.time.LocalDateTime;
import java.util.List;
public record ReferralDto(Long id,Long referrerEmployeeId,String referrerName,String candidateFullName,String candidateEmail,String candidatePhone,String linkedIn,Long targetJobId,String targetJobTitle,List<String> skills,String cvDocumentId,Referral.Status status,LocalDateTime createdAt){
    public static ReferralDto from(Referral r){return new ReferralDto(r.getId(),r.getReferrerEmployee().getId(),r.getReferrerEmployee().getFirstName()+" "+r.getReferrerEmployee().getLastName(),r.getCandidateFullName(),r.getCandidateEmail(),r.getCandidatePhone(),r.getLinkedIn(),r.getTargetJob()!=null?r.getTargetJob().getId():null,r.getTargetJob()!=null?r.getTargetJob().getTitle():null,r.getSkills(),r.getCvDocumentId(),r.getStatus(),r.getCreatedAt());}
}
