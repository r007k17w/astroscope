package com.astroscope.lab.controller;

import com.astroscope.lab.model.CollaborationGroup;
import com.astroscope.lab.model.User;
import com.astroscope.lab.repository.CollaborationGroupRepository;
import com.astroscope.lab.repository.UserRepository;
import com.astroscope.lab.service.ModerationDelegationService;
import com.astroscope.lab.service.ShareTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/groups")
public class CollaborationController {

    private final CollaborationGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ModerationDelegationService moderationDelegationService;
    private final ShareTokenService shareTokenService;
    private final CurrentUser currentUser;

    public CollaborationController(CollaborationGroupRepository groupRepository,
                                   UserRepository userRepository,
                                   ModerationDelegationService moderationDelegationService,
                                   ShareTokenService shareTokenService,
                                   CurrentUser currentUser) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.moderationDelegationService = moderationDelegationService;
        this.shareTokenService = shareTokenService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("groups", groupRepository.findAll());
        return "groups";
    }

    @GetMapping("/{slug}")
    public String view(@PathVariable String slug, Authentication authentication, Model model) {
        CollaborationGroup group = groupRepository.findBySlug(slug).orElseThrow();
        User actor = currentUser.require(authentication);
        model.addAttribute("group", group);
        model.addAttribute("canModerate", moderationDelegationService.canModerate(actor, group));
        model.addAttribute("inviteToken", shareTokenService.buildCollaborationInvite(group.getOwner(), slug));
        return "group-view";
    }

    @PostMapping("/{slug}/delegate")
    public String delegate(@PathVariable String slug,
                           @RequestParam String delegateUsername,
                           Authentication authentication) {
        CollaborationGroup group = groupRepository.findBySlug(slug).orElseThrow();
        User delegator = currentUser.require(authentication);
        User delegate = userRepository.findByUsername(delegateUsername).orElseThrow();
        moderationDelegationService.grant(delegator, delegate, group);
        return "redirect:/groups/" + slug;
    }

    @PostMapping("/{slug}/revoke")
    public String revoke(@PathVariable String slug,
                         @RequestParam String delegateUsername,
                         Authentication authentication) {
        CollaborationGroup group = groupRepository.findBySlug(slug).orElseThrow();
        User delegator = currentUser.require(authentication);
        User delegate = userRepository.findByUsername(delegateUsername).orElseThrow();
        moderationDelegationService.revoke(delegator, delegate, group);
        return "redirect:/groups/" + slug;
    }

    @PostMapping("/{slug}/moderate")
    public String moderateAction(@PathVariable String slug, Authentication authentication, Model model) {
        CollaborationGroup group = groupRepository.findBySlug(slug).orElseThrow();
        User actor = currentUser.require(authentication);
        if (!moderationDelegationService.canModerate(actor, group)) {
            model.addAttribute("error", "Not authorized");
            return "group-view";
        }
        model.addAttribute("message", "Moderation action recorded for " + slug);
        model.addAttribute("group", group);
        model.addAttribute("canModerate", true);
        return "group-view";
    }
}
