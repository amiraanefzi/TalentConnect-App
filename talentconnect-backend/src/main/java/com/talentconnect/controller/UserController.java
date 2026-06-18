package com.talentconnect.controller;
import com.talentconnect.dto.*;
import com.talentconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/users") @RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageDto<UserDto>>> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){ return ResponseEntity.ok(ApiResponse.ok(userService.findAll(PageRequest.of(page,size)))); }
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> profile(@AuthenticationPrincipal UserDetails p){ return ResponseEntity.ok(ApiResponse.ok(userService.findByEmail(p.getUsername()))); }
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(@AuthenticationPrincipal UserDetails p,@RequestBody Map<String,Object> body){ UserDto me=userService.findByEmail(p.getUsername()); return ResponseEntity.ok(ApiResponse.ok(userService.updateProfile(me.id(),body))); }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> findById(@PathVariable Long id){ return ResponseEntity.ok(ApiResponse.ok(userService.findById(id))); }
    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> create(@RequestBody Map<String,Object> body){ return ResponseEntity.status(201).body(ApiResponse.created(userService.create(body))); }
    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){ userService.delete(id); return ResponseEntity.noContent().build(); }
}
