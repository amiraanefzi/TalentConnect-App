package com.talentconnect.controller;
import com.talentconnect.dto.*;
import com.talentconnect.service.JobOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/jobs") @RequiredArgsConstructor
public class JobOfferController {
    private final JobOfferService jobOfferService;
    @GetMapping
    public ResponseEntity<ApiResponse<PageDto<JobOfferDto>>> list(@RequestParam(required=false) String q,@RequestParam(required=false) String location,@RequestParam(required=false) String department,@RequestParam(required=false) String status,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){ return ResponseEntity.ok(ApiResponse.ok(jobOfferService.search(q,location,department,status,PageRequest.of(page,size)))); }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobOfferDto>> findById(@PathVariable Long id){ return ResponseEntity.ok(ApiResponse.ok(jobOfferService.findById(id))); }
    @PostMapping @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<ApiResponse<JobOfferDto>> create(@RequestBody JobOfferDto dto){ return ResponseEntity.status(201).body(ApiResponse.created(jobOfferService.create(dto))); }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<ApiResponse<JobOfferDto>> update(@PathVariable Long id,@RequestBody JobOfferDto dto){ return ResponseEntity.ok(ApiResponse.ok(jobOfferService.update(id,dto))); }
    @PatchMapping("/{id}/status") @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<ApiResponse<JobOfferDto>> changeStatus(@PathVariable Long id,@RequestBody Map<String,String> body){ return ResponseEntity.ok(ApiResponse.ok(jobOfferService.changeStatus(id,body.get("status")))); }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){ jobOfferService.delete(id); return ResponseEntity.noContent().build(); }
}
