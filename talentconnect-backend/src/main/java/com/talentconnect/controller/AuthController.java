package com.talentconnect.controller;
import com.talentconnect.dto.*;
import com.talentconnect.entity.User;
import com.talentconnect.security.JwtService;
import com.talentconnect.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),request.password()));
        User user=userService.getByEmail(request.email());
        return ResponseEntity.ok(ApiResponse.ok(new AuthResponse(jwtService.generateToken(user.getEmail()),UserDto.from(user))));
    }
}
