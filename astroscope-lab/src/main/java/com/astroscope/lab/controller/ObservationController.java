package com.astroscope.lab.controller;

import com.astroscope.lab.model.Observation;
import com.astroscope.lab.model.User;
import com.astroscope.lab.service.ObservationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ObservationController {

    private final ObservationService observationService;
    private final CurrentUser currentUser;

    public ObservationController(ObservationService observationService, CurrentUser currentUser) {
        this.observationService = observationService;
        this.currentUser = currentUser;
    }

    @GetMapping("/feed")
    public String feed(Model model) {
        model.addAttribute("observations", observationService.publicFeed());
        return "feed";
    }

    @GetMapping("/observations/new")
    public String newForm() {
        return "observation-form";
    }

    @PostMapping("/observations")
    public String create(@RequestParam String title,
                         @RequestParam String body,
                         @RequestParam(defaultValue = "false") boolean isPrivate,
                         @RequestParam(required = false) String groupSlug,
                         @RequestParam(required = false) String targetObject,
                         Authentication authentication) {
        User user = currentUser.require(authentication);
        observationService.create(user, title, body, isPrivate, groupSlug, targetObject);
        return "redirect:/feed";
    }

    @GetMapping("/observations/{id}")
    public String view(@PathVariable Long id,
                       @RequestParam(required = false) String groupSlug,
                       Authentication authentication,
                       Model model) {
        Observation observation = observationService.findById(id).orElseThrow();
        User viewer = currentUser.optional(authentication);
        if (!observationService.canView(viewer, observation, groupSlug)) {
            model.addAttribute("denied", true);
            return "observation-view";
        }
        model.addAttribute("observation", observation);
        model.addAttribute("renderedBody", observationService.renderBodyHtml(observation));
        return "observation-view";
    }
}
