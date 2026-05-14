package tn.iteam.jobservice.jobs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.iteam.jobservice.common.errors.NotFoundException;
import tn.iteam.jobservice.jobs.api.JobOfferMapper;
import tn.iteam.jobservice.jobs.api.dto.JobOfferCreateRequest;
import tn.iteam.jobservice.jobs.api.dto.JobOfferResponse;
import tn.iteam.jobservice.jobs.api.dto.JobOfferUpdateRequest;
import tn.iteam.jobservice.jobs.domain.JobOffer;
import tn.iteam.jobservice.jobs.repo.JobOfferRepository;
import tn.iteam.jobservice.jobs.repo.JobOfferSpecifications;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobOfferService {

    private final JobOfferRepository repo;

    @Transactional
    public JobOfferResponse create(JobOfferCreateRequest req) {
        var entity = JobOfferMapper.toEntity(req);
        validateSalaryRange(entity.getSalaryMin(), entity.getSalaryMax());
        if (entity.isPublished()) {
            entity.setPublishedAt(Instant.now());
        }
        return JobOfferMapper.toResponse(repo.save(entity));
    }

    @Transactional(readOnly = true)
    public JobOfferResponse get(UUID id) {
        return JobOfferMapper.toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public JobOfferResponse getPublic(UUID id) {
        var entity = getEntity(id);
        if (!entity.isPublished()) {
            // Don't leak existence of unpublished offers to the public.
            throw new NotFoundException("Job offer not found: " + id);
        }
        return JobOfferMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<JobOfferResponse> list(Pageable pageable) {
        return repo.findAll(pageable).map(JobOfferMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<JobOfferResponse> listPublic(Specification<JobOffer> spec, Pageable pageable) {
        Specification<JobOffer> finalSpec = JobOfferSpecifications.publishedOnly();
        if (spec != null) {
            finalSpec = finalSpec.and(spec);
        }
        return repo.findAll(finalSpec, pageable).map(JobOfferMapper::toResponse);
    }

    @Transactional
    public JobOfferResponse update(UUID id, JobOfferUpdateRequest req) {
        var entity = getEntity(id);

        entity.setTitle(req.title());
        entity.setCompanyName(req.companyName());
        entity.setLocation(req.location());
        entity.setEmploymentType(req.employmentType());
        entity.setExperienceLevel(req.experienceLevel());
        entity.setRemote(Boolean.TRUE.equals(req.remote()));
        entity.setDescription(req.description());
        entity.setSalaryMin(req.salaryMin());
        entity.setSalaryMax(req.salaryMax());
        entity.setCurrency(req.currency());

        validateSalaryRange(entity.getSalaryMin(), entity.getSalaryMax());

        var wasPublished = entity.isPublished();
        var willBePublished = Boolean.TRUE.equals(req.published());
        entity.setPublished(willBePublished);
        if (!wasPublished && willBePublished) {
            entity.setPublishedAt(Instant.now());
        } else if (wasPublished && !willBePublished) {
            entity.setPublishedAt(null);
        }

        return JobOfferMapper.toResponse(repo.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Job offer not found: " + id);
        }
        repo.deleteById(id);
    }

    private JobOffer getEntity(UUID id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Job offer not found: " + id));
    }

    private void validateSalaryRange(Integer min, Integer max) {
        if (min != null && max != null && min > max) {
            throw new IllegalArgumentException("salaryMin must be <= salaryMax");
        }
    }
}
