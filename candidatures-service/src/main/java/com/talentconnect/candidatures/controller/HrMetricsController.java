package com.talentconnect.candidatures.controller;

import com.talentconnect.candidatures.domain.CandidatureStatus;
import com.talentconnect.candidatures.domain.CandidatureType;
import com.talentconnect.candidatures.infrastructure.jpa.CandidatureRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/hr")
@Tag(name = "HR Metrics", description = "Indicateurs RH sur les candidatures")
public class HrMetricsController {

    private final CandidatureRepository candidatureRepository;

    public HrMetricsController(CandidatureRepository candidatureRepository) {
        this.candidatureRepository = candidatureRepository;
    }

    /**
     * GET /api/hr/metrics — indicateurs candidatures (RH/ADMIN)
     */
    @GetMapping("/metrics")
    @PreAuthorize("hasAnyRole('RH','ADMIN')")
    @Operation(
            summary = "Métriques RH sur les candidatures",
            parameters = {
                    @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, required = true, example = "1"),
                    @Parameter(name = "X-Role",    in = ParameterIn.HEADER, required = true, example = "RH")
            }
    )
    public Map<String, Object> metrics() {
        long total      = candidatureRepository.count();
        long soumises   = candidatureRepository.countByStatus(CandidatureStatus.SOUMISE);
        long enCours    = candidatureRepository.countByStatus(CandidatureStatus.EN_COURS);
        long entretiens = candidatureRepository.countByStatus(CandidatureStatus.ENTRETIEN);
        long refusees   = candidatureRepository.countByStatus(CandidatureStatus.REFUSEE);
        long recrutees  = candidatureRepository.countByStatus(CandidatureStatus.RECRUTEE);
        long internal   = candidatureRepository.countByType(CandidatureType.INTERNE);
        long referrals  = candidatureRepository.countByType(CandidatureType.RECOMMANDATION);
        double rate     = total > 0 ? Math.round((double) recrutees / total * 10000.0) / 100.0 : 0.0;

        return Map.of(
                "totalCandidatures",  total,
                "soumises",           soumises,
                "enCours",            enCours,
                "entretiens",         entretiens,
                "refusees",           refusees,
                "recrutees",          recrutees,
                "internalCandidates", internal,
                "referrals",          referrals,
                "conversionRate",     rate
        );
    }
}


