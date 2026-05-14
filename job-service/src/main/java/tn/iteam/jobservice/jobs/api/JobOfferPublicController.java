package tn.iteam.jobservice.jobs.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.jobservice.jobs.api.dto.JobOfferResponse;
import tn.iteam.jobservice.jobs.domain.EmploymentType;
import tn.iteam.jobservice.jobs.domain.JobOffer;
import tn.iteam.jobservice.jobs.repo.JobOfferSpecifications;
import tn.iteam.jobservice.jobs.service.JobOfferService;

import java.util.UUID;
import java.util.Set;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class JobOfferPublicController {

    private final JobOfferService service;
    private static final Set<String> ALLOWED_SORTS = Set.of(
            "id",
            "title",
            "companyName",
            "location",
            "employmentType",
            "experienceLevel",
            "remote",
            "salaryMin",
            "salaryMax",
            "currency",
            "published",
            "publishedAt",
            "createdAt",
            "updatedAt"
    );

    @GetMapping
    public Page<JobOfferResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) EmploymentType employmentType,
            @RequestParam(required = false) Boolean remote,
            Pageable pageable
    ) {
        Specification<JobOffer> spec = (root, query, cb) -> cb.conjunction();
        spec = andIfPresent(spec, JobOfferSpecifications.keyword(q));
        spec = andIfPresent(spec, JobOfferSpecifications.hasLocation(location));
        spec = andIfPresent(spec, JobOfferSpecifications.hasEmploymentType(employmentType));
        spec = andIfPresent(spec, JobOfferSpecifications.isRemote(remote));
        return service.listPublic(spec, sanitize(pageable));
    }

    @GetMapping("/{id}")
    public JobOfferResponse get(@PathVariable UUID id) {
        return service.getPublic(id);
    }

    private static Specification<JobOffer> andIfPresent(Specification<JobOffer> base, Specification<JobOffer> next) {
        return next == null ? base : base.and(next);
    }

    private static Pageable sanitize(Pageable pageable) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            return pageable;
        }
        for (Sort.Order order : pageable.getSort()) {
            if (!ALLOWED_SORTS.contains(order.getProperty())) {
                return Pageable.ofSize(pageable.getPageSize())
                        .withPage(pageable.getPageNumber());
            }
        }
        return pageable;
    }
}
