package com.talentconnect.service;
import com.talentconnect.entity.Application;
import com.talentconnect.repository.ApplicationRepository;
import com.talentconnect.repository.ReferralRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
@Service @RequiredArgsConstructor
public class HrMetricsService {
    private final ApplicationRepository applicationRepository;
    private final ReferralRepository referralRepository;
    public Map<String,Object> getMetrics(){
        long total=applicationRepository.count();
        long internal=applicationRepository.countBySource(Application.Source.INTERNAL);
        long refs=referralRepository.count();
        long hired=applicationRepository.countByStatus(Application.Status.HIRED);
        double rate=total>0?(double)hired/total*100:0.0;
        return Map.of("totalApplications",total,"internalCandidates",internal,"referrals",refs,"avgTimeToHire",7,"conversionRate",Math.round(rate*100.0)/100.0);
    }
}
