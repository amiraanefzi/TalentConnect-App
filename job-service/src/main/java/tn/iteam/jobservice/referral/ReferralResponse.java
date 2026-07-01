package tn.iteam.jobservice.referral;

import java.time.Instant;
import java.util.List;

public record ReferralResponse(
        Long id,
        String referrerEmail,
        String candidateFullName,
        String candidateEmail,
        String candidatePhone,
        String linkedIn,
        String targetJobId,
        List<String> skills,
        String cvDocumentId,
        Referral.Status status,
        Instant createdAt
) {
    public static ReferralResponse from(Referral r) {
        return new ReferralResponse(
                r.getId(), r.getReferrerEmail(),
                r.getCandidateFullName(), r.getCandidateEmail(),
                r.getCandidatePhone(), r.getLinkedIn(),
                r.getTargetJobId(), r.getSkills(),
                r.getCvDocumentId(), r.getStatus(), r.getCreatedAt()
        );
    }
}

