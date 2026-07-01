package tn.iteam.jobservice.referral;

import java.util.List;

public record ReferralRequest(
        String candidateFullName,
        String candidateEmail,
        String candidatePhone,
        String linkedIn,
        String targetJobId,
        List<String> skills,
        String cvDocumentId,
        Referral.Status status
) {}

