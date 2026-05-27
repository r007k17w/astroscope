package com.astroscope.lab.controller;

import com.astroscope.lab.model.PaperShare;
import com.astroscope.lab.model.User;
import com.astroscope.lab.service.ShareTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/share")
public class ShareController {

    private final ShareTokenService shareTokenService;
    private final CurrentUser currentUser;

    public ShareController(ShareTokenService shareTokenService, CurrentUser currentUser) {
        this.shareTokenService = shareTokenService;
        this.currentUser = currentUser;
    }

    @GetMapping("/paper")
    public String paperShare(@RequestParam String token, Model model) {
        PaperShare share = shareTokenService.findPaperByToken(token).orElseThrow();
        model.addAttribute("share", share);
        return "paper-share";
    }

    @GetMapping("/new")
    public String newForm() {
        return "paper-share-form";
    }

    @PostMapping("/new")
    public String create(@RequestParam String title,
                         @RequestParam String abstractText,
                         Authentication authentication,
                         Model model) {
        User owner = currentUser.require(authentication);
        PaperShare share = shareTokenService.createPaperShare(owner, title, abstractText);
        model.addAttribute("share", share);
        return "paper-share-created";
    }
}
