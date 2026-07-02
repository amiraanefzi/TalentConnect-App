package tn.iteam.jobservice.hr;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.jobservice.audit.AuditEventRepository;
import tn.iteam.jobservice.jobs.repo.JobOfferRepository;
import tn.iteam.jobservice.referral.ReferralRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "HR Metrics", description = "Indicateurs RH sur les offres, cooptations et audit")
public class HrMetricsController {

    private static final Logger log = LoggerFactory.getLogger(HrMetricsController.class);

    private final JobOfferRepository jobOfferRepository;
    private final ReferralRepository referralRepository;
    private final AuditEventRepository auditEventRepository;

    /** GET /api/hr/metrics */
    @GetMapping("/metrics")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_ADMIN')")
    public Map<String, Object> metrics() {
        Map<String, Object> result = new HashMap<>();

        // Chaque compteur est isolé : une erreur DB sur un compteur ne fait pas échouer les autres
        result.put("totalJobOffers",    safeCount(() -> jobOfferRepository.count(),          "totalJobOffers"));
        result.put("publishedJobs",     safeCount(() -> jobOfferRepository.countPublished(),  "publishedJobs"));
        result.put("totalReferrals",    safeCount(() -> referralRepository.count(),           "totalReferrals"));
        result.put("hiredFromReferral", safeCount(() -> referralRepository.countHired(),      "hiredFromReferral"));
        result.put("auditEventsToday",  safeCount(() -> {
            Instant startOfDay = Instant.now().truncatedTo(ChronoUnit.DAYS);
            return auditEventRepository.countSince(startOfDay);
        }, "auditEventsToday"));
        result.put("totalAuditEvents",  safeCount(() -> auditEventRepository.count(),         "totalAuditEvents"));

        return result;
    }

    /** Exécute le compteur et retourne 0 en cas d'erreur (ex: zero-date MySQL, table vide, etc.) */
    private long safeCount(CountSupplier supplier, String metricName) {
        try {
            return supplier.get();
        } catch (RuntimeException e) {
            log.warn("Metric '{}' unavailable — returning 0. Cause: {}", metricName, e.getMessage());
            return 0L;
        }
    }

    @FunctionalInterface
    interface CountSupplier {
        long get() throws RuntimeException;
    }
}
