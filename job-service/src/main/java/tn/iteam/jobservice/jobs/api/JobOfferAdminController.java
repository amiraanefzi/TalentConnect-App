package tn.iteam.jobservice.jobs.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import tn.iteam.jobservice.jobs.api.dto.JobOfferCreateRequest;
import tn.iteam.jobservice.jobs.api.dto.JobOfferResponse;
import tn.iteam.jobservice.jobs.api.dto.JobOfferUpdateRequest;
import tn.iteam.jobservice.jobs.service.JobOfferService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class JobOfferAdminController {

    private final JobOfferService service;

    @PostMapping
    public ResponseEntity<JobOfferResponse> create(@Valid @RequestBody JobOfferCreateRequest req, UriComponentsBuilder ucb) {
        var created = service.create(req);
        URI location = ucb.path("/api/admin/jobs/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public JobOfferResponse update(@PathVariable UUID id, @Valid @RequestBody JobOfferUpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
