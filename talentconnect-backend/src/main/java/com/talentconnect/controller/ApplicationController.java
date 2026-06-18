package com.talentconnect.controller;
import com.talentconnect.dto.*;
import com.talentconnect.service.ApplicationService;
import com.talentconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/applications") @RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    private final UserService userService;
    @GetMapping @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<ApiResponse<PageDto<ApplicationDto>>> listAll(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){ return ResponseEntity.ok(ApiResponse.ok(applicationService.findAll(PageRequest.of(page,size)))); }
    @GetMapping("/mine") @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<PageDto<ApplicationDto>>> listMine(@AuthenticationPrincipal UserDetails p,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){ Long uid=userService.findByEmail(p.getUsername()).id(); return ResponseEntity.ok(ApiResponse.ok(applicationService.findMine(uid,PageRequest.of(page,size)))); }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationDto>> findById(@PathVariable Long id){ return ResponseEntity.ok(ApiResponse.ok(applicationService.findById(id))); }
    @PostMapping @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<ApplicationDto>> apply(@AuthenticationPrincipal UserDetails p,@RequestBody Map<String,Object> body){ Long uid=userService.findByEmail(p.getUsername()).id(); Long jobId=Long.parseLong(body.get("jobId").toString()); String source=(String)body.getOrDefault("source","INTERNAL"); return ResponseEntity.status(201).body(ApiResponse.created(applicationService.apply(uid,jobId,source,p.getUsername()))); }
    @PatchMapping("/{id}/status") @PreAuthorize("hasAnyRole('HR','ADMIN')")
    public ResponseEntity<ApiResponse<ApplicationDto>> changeStatus(@PathVariable Long id,@RequestBody Map<String,String> body,@AuthenticationPrincipal UserDetails p){ String role=p.getAuthorities().iterator().next().getAuthority().replace("ROLE_",""); return ResponseEntity.ok(ApiResponse.ok(applicationService.changeStatus(id,body.get("status"),p.getUsername(),role))); }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> withdraw(@PathVariable Long id,@AuthenticationPrincipal UserDetails p){ Long uid=userService.findByEmail(p.getUsername()).id(); applicationService.delete(id,uid); return ResponseEntity.noContent().build(); }
}
