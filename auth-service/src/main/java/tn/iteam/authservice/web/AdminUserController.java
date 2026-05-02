package tn.iteam.authservice.web;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.authservice.user.Role;
import tn.iteam.authservice.user.RoleParser;
import tn.iteam.authservice.user.User;
import tn.iteam.authservice.user.UserService;
import tn.iteam.authservice.user.dto.RegisterRequest;
import tn.iteam.authservice.user.dto.UpdateRolesRequest;
import tn.iteam.authservice.user.dto.UserDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> list() {
        return userService.listAll().stream().map(AdminUserController::toDto).toList();
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.email(), request.password(), Set.of(Role.EMPLOYE));
        return toDto(user);
    }

    @PatchMapping("/{id}/roles")
    public UserDto updateRoles(@PathVariable Long id, @Valid @RequestBody UpdateRolesRequest request) {
        Set<Role> roles = request.roles().stream().map(RoleParser::parse).collect(Collectors.toSet());
        return toDto(userService.updateRoles(id, roles));
    }

    @PatchMapping("/{id}/enabled/{enabled}")
    public UserDto setEnabled(@PathVariable Long id, @PathVariable boolean enabled) {
        return toDto(userService.setEnabled(id, enabled));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    private static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                user.isEnabled(),
                user.getCreatedAt()
        );
    }
}
