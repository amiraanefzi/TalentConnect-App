package com.talentconnect.controller;
import com.talentconnect.dto.*;
import com.talentconnect.entity.AuditEvent;
import com.talentconnect.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/audit") @RequiredArgsConstructor @PreAuthorize("hasAnyRole('HR','ADMIN')")
public class AuditController {
    private final AuditService auditService;
    @GetMapping
    public ResponseEntity<ApiResponse<PageDto<AuditEvent>>> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,@RequestParam(defaultValue="timestamp") String sortBy,@RequestParam(defaultValue="desc") String dir){
        Sort sort=dir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        return ResponseEntity.ok(ApiResponse.ok(auditService.findAll(PageRequest.of(page,size,sort))));
    }
}
