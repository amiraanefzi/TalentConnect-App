package com.talentconnect.service;
import com.talentconnect.dto.JobOfferDto;
import com.talentconnect.dto.PageDto;
import com.talentconnect.entity.JobOffer;
import com.talentconnect.exception.ResourceNotFoundException;
import com.talentconnect.repository.JobOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
@Service @RequiredArgsConstructor
public class JobOfferService {
    private final JobOfferRepository jobOfferRepository;
    public PageDto<JobOfferDto> search(String q,String location,String department,String statusStr,Pageable pageable){
        JobOffer.Status status=statusStr!=null?JobOffer.Status.valueOf(statusStr.toUpperCase()):null;
        return PageDto.from(jobOfferRepository.search(q,location,department,status,pageable),JobOfferDto::from);
    }
    public JobOfferDto findById(Long id){ return JobOfferDto.from(getOrThrow(id)); }
    @Transactional
    public JobOfferDto create(JobOfferDto dto){
        JobOffer o=JobOffer.builder().title(dto.title()).department(dto.department()).location(dto.location()).description(dto.description()).employmentType(dto.employmentType()).seniority(dto.seniority()).status(dto.status()!=null?dto.status():JobOffer.Status.DRAFT).requirements(dto.requirements()!=null?dto.requirements():List.of()).tags(dto.tags()!=null?dto.tags():List.of()).publishedAt(dto.publishedAt()).closingAt(dto.closingAt()).hiringManager(dto.hiringManager()).recommendedScore(dto.recommendedScore()).build();
        return JobOfferDto.from(jobOfferRepository.save(o));
    }
    @Transactional
    public JobOfferDto update(Long id,JobOfferDto dto){
        JobOffer o=getOrThrow(id);
        if(dto.title()!=null) o.setTitle(dto.title());
        if(dto.department()!=null) o.setDepartment(dto.department());
        if(dto.location()!=null) o.setLocation(dto.location());
        if(dto.description()!=null) o.setDescription(dto.description());
        if(dto.employmentType()!=null) o.setEmploymentType(dto.employmentType());
        if(dto.seniority()!=null) o.setSeniority(dto.seniority());
        if(dto.requirements()!=null) o.setRequirements(dto.requirements());
        if(dto.tags()!=null) o.setTags(dto.tags());
        if(dto.closingAt()!=null) o.setClosingAt(dto.closingAt());
        return JobOfferDto.from(jobOfferRepository.save(o));
    }
    @Transactional
    public JobOfferDto changeStatus(Long id,String statusStr){
        JobOffer o=getOrThrow(id);
        JobOffer.Status ns=JobOffer.Status.valueOf(statusStr.toUpperCase());
        o.setStatus(ns);
        if(ns==JobOffer.Status.OPEN&&o.getPublishedAt()==null) o.setPublishedAt(LocalDateTime.now());
        return JobOfferDto.from(jobOfferRepository.save(o));
    }
    @Transactional
    public void delete(Long id){ if(!jobOfferRepository.existsById(id)) throw new ResourceNotFoundException("Offre introuvable: "+id); jobOfferRepository.deleteById(id); }
    public JobOffer getOrThrow(Long id){ return jobOfferRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Offre introuvable: "+id)); }
}
