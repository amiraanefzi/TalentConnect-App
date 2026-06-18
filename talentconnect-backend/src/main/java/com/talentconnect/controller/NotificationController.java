package com.talentconnect.controller;
import com.talentconnect.dto.*;
import com.talentconnect.service.NotificationService;
import com.talentconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/notifications") @RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto>>> list(@AuthenticationPrincipal UserDetails p){ Long uid=userService.findByEmail(p.getUsername()).id(); return ResponseEntity.ok(ApiResponse.ok(notificationService.findForUser(uid))); }
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDto>> markRead(@PathVariable Long id,@AuthenticationPrincipal UserDetails p){ Long uid=userService.findByEmail(p.getUsername()).id(); return ResponseEntity.ok(ApiResponse.ok(notificationService.markRead(id,uid))); }
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead(@AuthenticationPrincipal UserDetails p){ Long uid=userService.findByEmail(p.getUsername()).id(); notificationService.markAllRead(uid); return ResponseEntity.ok(ApiResponse.ok(null)); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,@AuthenticationPrincipal UserDetails p){ Long uid=userService.findByEmail(p.getUsername()).id(); notificationService.delete(id,uid); return ResponseEntity.noContent().build(); }
}