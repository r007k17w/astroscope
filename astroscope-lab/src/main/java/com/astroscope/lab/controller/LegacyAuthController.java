package com.astroscope.lab.controller;

import com.astroscope.lab.model.User;
import com.astroscope.lab.platform.auth.ObservatoryIntegrationAuthGateway;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LegacyAuthController {

    private final ObservatoryIntegrationAuthGateway integrationAuthGateway;

    public LegacyAuthController(ObservatoryIntegrationAuthGateway integrationAuthGateway) {
        this.integrationAuthGateway = integrationAuthGateway;
    }

    @GetMapping("/legacy/login")
    public String legacyLoginForm() {
        return "legacy-login";
    }

    @PostMapping("/legacy/login")
    public String legacyLogin(@RequestParam("clientId") String clientId,
                              @RequestParam("clientSecret") String clientSecret,
                              HttpSession session,
                              Model model) {
        Optional<User> user = integrationAuthGateway.authenticateIntegrationClient(clientId, clientSecret);
        if (user.isPresent()) {
            session.setAttribute("legacyUser", user.get().getUsername());
            return "redirect:/feed";
        }
        model.addAttribute("error", "Integration credentials rejected");
        return "legacy-login";
    }
}
