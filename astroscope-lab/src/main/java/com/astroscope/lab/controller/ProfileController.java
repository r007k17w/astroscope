package com.astroscope.lab.controller;

import com.astroscope.lab.model.User;
import com.astroscope.lab.repository.UserRepository;
import com.astroscope.lab.service.ProfileUpdateService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {

    private final ProfileUpdateService profileUpdateService;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public ProfileController(ProfileUpdateService profileUpdateService,
                             UserRepository userRepository,
                             CurrentUser currentUser) {
        this.profileUpdateService = profileUpdateService;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = currentUser.require(authentication);
        model.addAttribute("user", user);
        model.addAttribute("safeUsername", profileUpdateService.sanitizeUsernameForDisplay(user.getUsername()));
        return "profile";
    }

    @PostMapping("/profile")
    public String update(@RequestParam String displayName,
                         @RequestParam String institution,
                         @RequestParam String bio,
                         Authentication authentication) {
        User user = currentUser.require(authentication);
        profileUpdateService.updateProfileForm(user, displayName, institution, bio);
        return "redirect:/profile";
    }

    @GetMapping("/profiles/{username}")
    public String publicProfile(@PathVariable String username, Model model) {
        User user = userRepository.findByUsername(username).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("safeUsername", profileUpdateService.sanitizeUsernameForDisplay(user.getUsername()));
        return "profile-public";
    }
}
