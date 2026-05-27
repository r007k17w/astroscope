package com.astroscope.lab.controller;

import com.astroscope.lab.model.User;
import com.astroscope.lab.service.ProfileUpdateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileApiController {

    private final ProfileUpdateService profileUpdateService;
    private final CurrentUser currentUser;

    public ProfileApiController(ProfileUpdateService profileUpdateService, CurrentUser currentUser) {
        this.profileUpdateService = profileUpdateService;
        this.currentUser = currentUser;
    }

    @PatchMapping
    public ResponseEntity<?> patch(@RequestBody Map<String, Object> payload, Authentication authentication) {
        User user = currentUser.require(authentication);
        return profileUpdateService.patchProfileApi(user.getUsername(), payload)
                .map(u -> ResponseEntity.ok(Map.of(
                        "username", u.getUsername(),
                        "role", u.getRole().name(),
                        "verified", u.isVerified())))
                .orElse(ResponseEntity.notFound().build());
    }
}
