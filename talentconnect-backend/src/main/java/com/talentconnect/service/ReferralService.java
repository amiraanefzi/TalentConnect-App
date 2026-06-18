package com.talentconnect.service;
import com.talentconnect.dto.ReferralDto;
import com.talentconnect.entity.Referral;
import com.talentconnect.exception.ForbiddenException;
import com.talentconnect.exception.ResourceNotFoundException;
import com.talentconnect.repository.ReferralRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor
public class ReferralService {
    private final ReferralRepository referralRepository;
    private final UserService userService;
    private final JobOfferService jobOfferService;
    public List<ReferralDto> findMine(Long uid){ return referralRepository.findByReferrerEmployeeId(uid).stream().map(ReferralDto::from).toList(); }
    @Transactional
    public ReferralDto create(ReferralDto dto,Long uid){
        Referral r=Referral.builder().referrerEmployee(userService.getOrThrow(uid)).candidateFullName(dto.candidateFullName()).candidateEmail(dto.candidateEmail()).candidatePhone(dto.candidatePhone()).linkedIn(dto.linkedIn()).targetJob(dto.targetJobId()!=null?jobOfferService.getOrThrow(dto.targetJobId()):null).skills(dto.skills()!=null?dto.skills():List.of()).cvDocumentId(dto.cvDocumentId()).status(dto.status()!=null?dto.status():Referral.Status.DRAFT).build();
        return ReferralDto.from(referralRepository.save(r));
    }
    @Transactional
    public void delete(Long id,Long uid){
        Referral r=referralRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Cooptation introuvable: "+id));
        if(!r.getReferrerEmployee().getId().equals(uid)) throw new ForbiddenException("Vous ne pouvez supprimer que vos propres cooptations");
        referralRepository.deleteById(id);
    }
}
