package com.talentconnect.controller;
import com.talentconnect.dto.*;
import com.talentconnect.service.ReferralService;
import com.talentconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/referrals") @RequiredArgsConstructor
public class ReferralController {
    private final ReferralService referralService;
    private final UserService userService;
    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<ReferralDto>>> listMine(@AuthenticationPrincipal UserDetails p){ Long uid=userService.findByEmail(p.getUsername()).id(); return ResponseEntity.ok(ApiResponse.ok(referralService.findMine(uid))); }
    @PostMapping
    public ResponseEntity<ApiResponse<ReferralDto>> create(@RequestBody ReferralDto dto,@AuthenticationPrincipal UserDetails p){ Long uid=userService.findByEmail(p.getUsername()).id(); return ResponseEntity.status(201).body(ApiResponse.created(referralService.create(dto,uid))); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,@AuthenticationPrincipal UserDetails p){ Long uid=userService.findByEmail(p.getUsername()).id(); referralService.delete(id,uid); return ResponseEntity.noContent().build(); }
}