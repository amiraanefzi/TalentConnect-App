package tn.iteam.jobservice.audit;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Audit", description = "Journal des actions RH")
public class AuditController {

    private final AuditService auditService;

    /** GET /api/audit — liste paginée des événements (RH/ADMIN) */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_ADMIN')")
    public Page<AuditEvent> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        Sort sort = dir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return auditService.findAll(PageRequest.of(page, size, sort));
    }
}

