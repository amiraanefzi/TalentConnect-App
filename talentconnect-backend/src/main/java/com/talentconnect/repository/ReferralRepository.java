package com.talentconnect.repository;
import com.talentconnect.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ReferralRepository extends JpaRepository<Referral,Long> {
    List<Referral> findByReferrerEmployeeId(Long referrerEmployeeId);
}
