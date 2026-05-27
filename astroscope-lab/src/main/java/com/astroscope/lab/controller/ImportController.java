package com.astroscope.lab.controller;

import com.astroscope.lab.service.ExternalFetchClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/import")
public class ImportController {

    private final ExternalFetchClient externalFetchClient;

    public ImportController(ExternalFetchClient externalFetchClient) {
        this.externalFetchClient = externalFetchClient;
    }

    @GetMapping
    public String form() {
        return "import";
    }

    @PostMapping
    public String importMetadata(@RequestParam String url, Model model) {
        try {
            model.addAttribute("result", externalFetchClient.fetchMetadata(url));
            model.addAttribute("success", true);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "import";
    }
}
