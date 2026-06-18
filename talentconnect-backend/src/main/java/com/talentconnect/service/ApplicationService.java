package com.talentconnect.service;
import com.talentconnect.dto.ApplicationDto;
import com.talentconnect.dto.PageDto;
import com.talentconnect.entity.Application;
import com.talentconnect.entity.AuditEvent;
import com.talentconnect.exception.DuplicateResourceException;
import com.talentconnect.exception.ForbiddenException;
import com.talentconnect.exception.ResourceNotFoundException;
import com.talentconnect.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JobOfferService jobOfferService;
    private final UserService userService;
    private final AuditService auditService;
    public PageDto<ApplicationDto> findAll(Pageable p){ return PageDto.from(applicationRepository.findAll(p),ApplicationDto::from); }
    public PageDto<ApplicationDto> findMine(Long uid,Pageable p){ return PageDto.from(applicationRepository.findByEmployeeId(uid,p),ApplicationDto::from); }
    public ApplicationDto findById(Long id){ return ApplicationDto.from(getOrThrow(id)); }
    @Transactional
    public ApplicationDto apply(Long uid,Long jobId,String source,String actorEmail){
        if(applicationRepository.existsByEmployeeIdAndJobId(uid,jobId)) throw new DuplicateResourceException("Vous avez deja postule a cette offre");
        Application app=Application.builder().job(jobOfferService.getOrThrow(jobId)).employee(userService.getOrThrow(uid)).source(source!=null?Application.Source.valueOf(source):Application.Source.INTERNAL).status(Application.Status.SUBMITTED).build();
        Application saved=applicationRepository.save(app);
        auditService.log(actorEmail,"EMPLOYEE","APPLY",AuditEvent.EntityType.APPLICATION,String.valueOf(saved.getId()),"Candidature pour offre "+jobId);
        return ApplicationDto.from(saved);
    }
    @Transactional
    public ApplicationDto changeStatus(Long id,String statusStr,String actorEmail,String actorRole){
        Application app=getOrThrow(id);
        app.setStatus(Application.Status.valueOf(statusStr.toUpperCase()));
        Application saved=applicationRepository.save(app);
        auditService.log(actorEmail,actorRole,"CHANGE_STATUS",AuditEvent.EntityType.APPLICATION,String.valueOf(id),"Statut -> "+saved.getStatus());
        return ApplicationDto.from(saved);
    }
    @Transactional
    public void delete(Long id,Long uid){
        Application app=getOrThrow(id);
        if(app.getEmployee()==null||!app.getEmployee().getId().equals(uid)) throw new ForbiddenException("Vous ne pouvez retirer que vos propres candidatures");
        applicationRepository.deleteById(id);
    }
    public Application getOrThrow(Long id){ return applicationRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Candidature introuvable: "+id)); }
}
