package tn.iteam.jobservice.referral;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/referrals")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Referrals", description = "Gestion des cooptations")
public class ReferralController {

    private final ReferralService referralService;

    /** GET /api/referrals/mine — mes cooptations */
    @GetMapping("/mine")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYE','ROLE_RH','ROLE_ADMIN')")
    public List<ReferralResponse> listMine(@AuthenticationPrincipal UserDetails principal) {
        return referralService.findMine(principal.getUsername());
    }

    /** GET /api/referrals — toutes les cooptations (RH/ADMIN) */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_ADMIN')")
    public List<ReferralResponse> listAll() {
        return referralService.findAll();
    }

    /** POST /api/referrals — créer une cooptation */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYE','ROLE_RH','ROLE_ADMIN')")
    public ReferralResponse create(
            @RequestBody ReferralRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        return referralService.create(principal.getUsername(), request);
    }

    /** PATCH /api/referrals/{id}/status — changer le statut (RH/ADMIN) */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_RH','ROLE_ADMIN')")
    public ReferralResponse updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        return referralService.updateStatus(id, Referral.Status.valueOf(body.get("status").toUpperCase()));
    }

    /** DELETE /api/referrals/{id} — supprimer ma cooptation */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYE','ROLE_RH','ROLE_ADMIN')")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        referralService.delete(id, principal.getUsername());
    }
}


