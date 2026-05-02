package tn.iteam.authservice.web;

import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.authservice.security.JwtService;
import tn.iteam.authservice.user.Role;
import tn.iteam.authservice.user.User;
import tn.iteam.authservice.user.UserService;
import tn.iteam.authservice.user.dto.AuthResponse;
import tn.iteam.authservice.user.dto.LoginRequest;
import tn.iteam.authservice.user.dto.RegisterRequest;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.email(), request.password(), Set.of(Role.EMPLOYE));
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        UserDetails principal = (UserDetails) auth.getPrincipal();
        String token = jwtService.generate(principal);
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), principal.getAuthorities().stream()
                .map(a -> a.getAuthority().replaceFirst("^ROLE_", ""))
                .collect(Collectors.toSet()));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        UserDetails principal = (UserDetails) auth.getPrincipal();
        User user = userService.getByEmail(request.email());
        String token = jwtService.generate(principal);
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), principal.getAuthorities().stream()
                .map(a -> a.getAuthority().replaceFirst("^ROLE_", ""))
                .collect(Collectors.toSet()));
    }
}
