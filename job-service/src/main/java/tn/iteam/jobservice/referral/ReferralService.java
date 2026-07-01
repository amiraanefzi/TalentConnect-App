package tn.iteam.jobservice.referral;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.iteam.jobservice.common.errors.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReferralService {

    private final ReferralRepository referralRepository;

    public List<ReferralResponse> findMine(String referrerEmail) {
        return referralRepository.findByReferrerEmailOrderByCreatedAtDesc(referrerEmail)
                .stream().map(ReferralResponse::from).toList();
    }

    public List<ReferralResponse> findAll() {
        return referralRepository.findAll().stream().map(ReferralResponse::from).toList();
    }

    @Transactional
    public ReferralResponse create(String referrerEmail, ReferralRequest req) {
        Referral referral = Referral.builder()
                .referrerEmail(referrerEmail)
                .candidateFullName(req.candidateFullName())
                .candidateEmail(req.candidateEmail())
                .candidatePhone(req.candidatePhone())
                .linkedIn(req.linkedIn())
                .targetJobId(req.targetJobId())
                .skills(req.skills() != null ? req.skills() : List.of())
                .cvDocumentId(req.cvDocumentId())
                .status(req.status() != null ? req.status() : Referral.Status.DRAFT)
                .build();
        return ReferralResponse.from(referralRepository.save(referral));
    }

    @Transactional
    public ReferralResponse updateStatus(Long id, Referral.Status newStatus) {
        Referral r = referralRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cooptation introuvable: " + id));
        r.setStatus(newStatus);
        return ReferralResponse.from(referralRepository.save(r));
    }

    @Transactional
    public void delete(Long id, String referrerEmail) {
        Referral r = referralRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cooptation introuvable: " + id));
        if (!r.getReferrerEmail().equalsIgnoreCase(referrerEmail)) {
            throw new IllegalStateException("Vous ne pouvez supprimer que vos propres cooptations");
        }
        referralRepository.deleteById(id);
    }
}

