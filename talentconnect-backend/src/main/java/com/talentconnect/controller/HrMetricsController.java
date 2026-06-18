package com.talentconnect.controller;
import com.talentconnect.dto.ApiResponse;
import com.talentconnect.service.HrMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/hr") @RequiredArgsConstructor @PreAuthorize("hasAnyRole('HR','ADMIN')")
public class HrMetricsController {
    private final HrMetricsService hrMetricsService;
    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<Map<String,Object>>> metrics(){ return ResponseEntity.ok(ApiResponse.ok(hrMetricsService.getMetrics())); }
}
