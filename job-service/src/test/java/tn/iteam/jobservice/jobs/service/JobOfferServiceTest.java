package tn.iteam.jobservice.jobs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import tn.iteam.jobservice.common.errors.NotFoundException;
import tn.iteam.jobservice.jobs.api.JobOfferMapper;
import tn.iteam.jobservice.jobs.api.dto.JobOfferCreateRequest;
import tn.iteam.jobservice.jobs.api.dto.JobOfferResponse;
import tn.iteam.jobservice.jobs.api.dto.JobOfferUpdateRequest;
import tn.iteam.jobservice.jobs.domain.JobOffer;
import tn.iteam.jobservice.jobs.repo.JobOfferRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JobOfferServiceTest {

    @Mock
    private JobOfferRepository repo;

    @InjectMocks
    private JobOfferService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldSaveJobOffer() {
        JobOfferCreateRequest request = new JobOfferCreateRequest(
                "Software Engineer",
                "TechCorp",
                "Paris",
                null,
                null,
                false,
                "Description",
                3000,
                5000,
                "EUR",
                false
        );

        JobOffer entity = JobOfferMapper.toEntity(request);
        when(repo.save(any(JobOffer.class))).thenReturn(entity);

        JobOfferResponse response = service.create(request);

        assertNotNull(response);
        verify(repo, times(1)).save(any(JobOffer.class));
    }

    @Test
    void get_shouldReturnJobOffer() {
        UUID id = UUID.randomUUID();
        JobOffer entity = new JobOffer();
        entity.setId(id);
        when(repo.findById(id)).thenReturn(Optional.of(entity));

        JobOfferResponse response = service.get(id);

        assertNotNull(response);
        assertEquals(id, response.id());
        verify(repo, times(1)).findById(id);
    }

    @Test
    void getPublic_shouldThrowNotFoundExceptionForUnpublishedOffer() {
        UUID id = UUID.randomUUID();
        JobOffer entity = new JobOffer();
        entity.setId(id);
        entity.setPublished(false);
        when(repo.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(NotFoundException.class, () -> service.getPublic(id));
    }

    @Test
    void list_shouldReturnPagedJobOffers() {
        Page<JobOffer> page = new PageImpl<>(List.of(new JobOffer()));
        when(repo.findAll(any(Pageable.class))).thenReturn(page);

        Page<JobOfferResponse> response = service.list(Pageable.unpaged());

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(repo, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void delete_shouldThrowNotFoundExceptionIfJobOfferDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.delete(id));
    }
}
