package tn.iteam.authservice.web;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.iteam.authservice.user.Role;
import tn.iteam.authservice.user.RoleParser;
import tn.iteam.authservice.user.User;
import tn.iteam.authservice.user.UserService;
import tn.iteam.authservice.user.dto.AdminCreateUserRequest;
import tn.iteam.authservice.user.dto.UpdateProfileRequest;
import tn.iteam.authservice.user.dto.UserDto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    /** GET /api/users/profile — profil de l'utilisateur connecté */
    @GetMapping("/profile")
    public UserDto getProfile(@AuthenticationPrincipal UserDetails principal) {
        return userService.toDto(userService.getByEmail(principal.getUsername()));
    }

    /** PUT /api/users/profile — mise à jour du profil (champs étendus inclus) */
    @PutMapping("/profile")
    public UserDto updateProfile(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody UpdateProfileRequest request
    ) {
        User current = userService.getByEmail(principal.getUsername());
        return userService.toDto(userService.updateProfile(current.getId(), request));
    }

    /** GET /api/users — liste tous les utilisateurs (ADMIN) */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<UserDto> all = userService.listAll().stream().map(userService::toDto).toList();
        int total = all.size();
        int from  = Math.min(page * size, total);
        int to    = Math.min(from + size, total);
        List<UserDto> content = all.subList(from, to);
        return Map.of(
                "content", content,
                "page", page,
                "size", size,
                "totalElements", total,
                "totalPages", (int) Math.ceil((double) total / size)
        );
    }

    /** GET /api/users/{id} — profil d'un utilisateur spécifique (ADMIN/RH) */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RH')")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        try { return ResponseEntity.ok(userService.toDto(userService.getById(id))); }
        catch (IllegalArgumentException e) { return ResponseEntity.notFound().build(); }
    }

    /** POST /api/users — créer un utilisateur (ADMIN) */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        Set<Role> roles = (request.roles() == null || request.roles().isEmpty())
                ? Set.of(Role.EMPLOYE)
                : request.roles().stream().map(RoleParser::parse).collect(Collectors.toSet());
        User created = userService.register(request.email(), request.password(), roles);
        // Mise à jour du profil en une seule requête si des champs de profil sont fournis
        if (request.firstName() != null || request.lastName() != null || request.department() != null
                || request.location() != null || request.title() != null || request.experienceYears() != null) {
            UpdateProfileRequest profileReq = new UpdateProfileRequest(
                    request.firstName(), request.lastName(), request.department(),
                    request.location(), request.title(), request.experienceYears(),
                    request.avatarUrl(), request.phone(), request.address(), request.bio(),
                    request.linkedinUrl(), request.githubUrl(),
                    request.languages(), request.skills(),
                    request.formations(), request.experiences()
            );
            created = userService.updateProfile(created.getId(), profileReq);
        }
        return userService.toDto(created);
    }

    /** DELETE /api/users/{id} — supprimer un utilisateur (ADMIN) */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }
}


