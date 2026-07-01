package com.talentconnect.candidatures.controller;

import com.talentconnect.candidatures.application.NotificationService;
import com.talentconnect.candidatures.dto.NotificationResponse;
import com.talentconnect.candidatures.infrastructure.security.CurrentSecurity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Gestion des notifications utilisateur")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentSecurity currentSecurity;

    public NotificationController(NotificationService notificationService, CurrentSecurity currentSecurity) {
        this.notificationService = notificationService;
        this.currentSecurity     = currentSecurity;
    }

    /** GET /api/notifications — mes notifications */
    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE','RH','ADMIN')")
    @Operation(
            summary = "Lister mes notifications",
            parameters = {
                    @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, required = true, example = "42"),
                    @Parameter(name = "X-Role",    in = ParameterIn.HEADER, required = true, example = "EMPLOYEE")
            }
    )
    public List<NotificationResponse> list() {
        return notificationService.findForUser(currentSecurity.userId(), currentSecurity.role());
    }

    /** GET /api/notifications/unread-count — nombre de non lues */
    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('EMPLOYEE','RH','ADMIN')")
    public Map<String, Long> unreadCount() {
        return Map.of("count", notificationService.countUnread(currentSecurity.userId(), currentSecurity.role()));
    }

    /** PATCH /api/notifications/{id}/read — marquer une notification comme lue */
    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('EMPLOYEE','RH','ADMIN')")
    @Operation(
            summary = "Marquer une notification comme lue",
            parameters = {
                    @Parameter(name = "X-User-Id", in = ParameterIn.HEADER, required = true, example = "42"),
                    @Parameter(name = "X-Role",    in = ParameterIn.HEADER, required = true, example = "EMPLOYEE")
            }
    )
    public NotificationResponse markRead(@PathVariable Long id) {
        return notificationService.markRead(id, currentSecurity.userId(), currentSecurity.role());
    }

    /** PATCH /api/notifications/read-all — tout marquer comme lu */
    @PatchMapping("/read-all")
    @PreAuthorize("hasAnyRole('EMPLOYEE','RH','ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllRead() {
        notificationService.markAllRead(currentSecurity.userId(), currentSecurity.role());
    }

    /** DELETE /api/notifications/{id} — supprimer une notification */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','RH','ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        notificationService.delete(id, currentSecurity.userId(), currentSecurity.role());
    }
}

