package com.talentconnect.service;
import com.talentconnect.dto.PageDto;
import com.talentconnect.entity.AuditEvent;
import com.talentconnect.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class AuditService {
    private final AuditEventRepository auditEventRepository;
    public PageDto<AuditEvent> findAll(Pageable p){ return PageDto.from(auditEventRepository.findAllByOrderByTimestampDesc(p),e->e); }
    @Transactional
    public void log(String actor,String actorRole,String action,AuditEvent.EntityType entityType,String entityId,String details){
        auditEventRepository.save(AuditEvent.builder().actor(actor).actorRole(actorRole).action(action).entityType(entityType).entityId(entityId).details(details).build());
    }
}
