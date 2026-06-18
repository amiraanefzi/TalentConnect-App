package com.talentconnect.service;
import com.talentconnect.dto.NotificationDto;
import com.talentconnect.entity.Notification;
import com.talentconnect.exception.ForbiddenException;
import com.talentconnect.exception.ResourceNotFoundException;
import com.talentconnect.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    public List<NotificationDto> findForUser(Long uid){ return notificationRepository.findByUserIdOrderByCreatedAtDesc(uid).stream().map(NotificationDto::from).toList(); }
    @Transactional
    public NotificationDto markRead(Long id,Long uid){
        Notification n=notificationRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Notification introuvable: "+id));
        if(!n.getUser().getId().equals(uid)) throw new ForbiddenException("Acces interdit");
        n.setRead(true);
        return NotificationDto.from(notificationRepository.save(n));
    }
    @Transactional
    public void markAllRead(Long uid){ notificationRepository.markAllReadByUserId(uid); }
    @Transactional
    public void delete(Long id,Long uid){
        Notification n=notificationRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Notification introuvable: "+id));
        if(!n.getUser().getId().equals(uid)) throw new ForbiddenException("Acces interdit");
        notificationRepository.deleteById(id);
    }
    @Transactional
    public void push(Long uid,Notification.NotifType type,String title,String message,String deepLink){
        Notification n=Notification.builder().user(userService.getOrThrow(uid)).type(type).title(title).message(message).deepLink(deepLink).build();
        notificationRepository.save(n);
    }
}
