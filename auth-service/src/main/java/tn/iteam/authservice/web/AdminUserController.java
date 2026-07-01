package tn.iteam.authservice.web;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.iteam.authservice.user.Role;
import tn.iteam.authservice.user.RoleParser;
import tn.iteam.authservice.user.User;
import tn.iteam.authservice.user.UserService;
import tn.iteam.authservice.user.dto.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> list() {
        return userService.listAll().stream().map(userService::toDto).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody AdminCreateUserRequest request) {
        Set<Role> roles = (request.roles() == null || request.roles().isEmpty())
                ? Set.of(Role.EMPLOYE)
                : request.roles().stream().map(RoleParser::parse).collect(Collectors.toSet());
        User created = userService.register(request.email(), request.password(), roles);
        // Mise à jour du profil en une seule requête si des champs de profil sont fournis
        if (hasProfileFields(request)) {
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

    private boolean hasProfileFields(AdminCreateUserRequest r) {
        return r.firstName() != null || r.lastName() != null || r.department() != null
                || r.location() != null || r.title() != null || r.experienceYears() != null
                || r.phone() != null || r.bio() != null || r.skills() != null && !r.skills().isEmpty()
                || r.languages() != null && !r.languages().isEmpty();
    }

    /** PUT /api/admin/users/{id} — modifier le profil complet d'un utilisateur (sans rôles) */
    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody AdminUpdateUserRequest request) {
        UpdateProfileRequest profileReq = new UpdateProfileRequest(
                request.firstName(), request.lastName(), request.department(),
                request.location(), request.title(), request.experienceYears(),
                request.avatarUrl(), request.phone(), request.address(), request.bio(),
                request.linkedinUrl(), request.githubUrl(),
                request.languages(), request.skills(),
                request.formations(), request.experiences()
        );
        return userService.toDto(userService.updateProfile(id, profileReq));
    }

    @PatchMapping("/{id}/roles")
    public UserDto updateRoles(@PathVariable Long id, @Valid @RequestBody UpdateRolesRequest request) {
        Set<Role> roles = request.roles().stream().map(RoleParser::parse).collect(Collectors.toSet());
        return userService.toDto(userService.updateRoles(id, roles));
    }

    @PatchMapping("/{id}/enabled/{enabled}")
    public UserDto setEnabled(@PathVariable Long id, @PathVariable boolean enabled) {
        return userService.toDto(userService.setEnabled(id, enabled));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}

