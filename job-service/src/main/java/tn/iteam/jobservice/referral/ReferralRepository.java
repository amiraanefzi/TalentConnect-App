package tn.iteam.jobservice.referral;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReferralRepository extends JpaRepository<Referral, Long> {
    List<Referral> findByReferrerEmailOrderByCreatedAtDesc(String referrerEmail);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(r) FROM Referral r WHERE r.status = 'HIRED'")
    long countHired();
}

