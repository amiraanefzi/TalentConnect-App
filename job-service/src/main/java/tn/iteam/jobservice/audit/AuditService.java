package tn.iteam.jobservice.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public Page<AuditEvent> findAll(Pageable pageable) {
        return auditEventRepository.findAllByOrderByTimestampDesc(pageable);
    }

    @Transactional
    public void log(String actor, String actorRole, String action,
                    String entityType, String entityId, String details) {
        auditEventRepository.save(AuditEvent.builder()
                .actor(actor)
                .actorRole(actorRole)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build());
    }
}

