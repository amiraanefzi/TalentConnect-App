package tn.iteam.jobservice.jobs.repo;

import org.springframework.data.jpa.domain.Specification;
import tn.iteam.jobservice.jobs.domain.EmploymentType;
import tn.iteam.jobservice.jobs.domain.JobOffer;

public final class JobOfferSpecifications {
    private JobOfferSpecifications() {
    }

    public static Specification<JobOffer> publishedOnly() {
        return (root, query, cb) -> cb.isTrue(root.get("published"));
    }

    public static Specification<JobOffer> hasLocation(String location) {
        if (location == null || location.isBlank()) {
            return null;
        }
        var like = "%" + location.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("location")), like);
    }

    public static Specification<JobOffer> isRemote(Boolean remote) {
        if (remote == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("remote"), remote);
    }

    public static Specification<JobOffer> hasEmploymentType(EmploymentType employmentType) {
        if (employmentType == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("employmentType"), employmentType);
    }

    public static Specification<JobOffer> keyword(String q) {
        if (q == null || q.isBlank()) {
            return null;
        }
        var like = "%" + q.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(root.get("companyName")), like),
                cb.like(cb.lower(root.get("description")), like)
        );
    }
}
