package com.astroscope.lab.controller;

import com.astroscope.lab.model.User;
import com.astroscope.lab.service.AdminExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/export")
public class AdminExportController {

    private final AdminExportService adminExportService;

    public AdminExportController(AdminExportService adminExportService) {
        this.adminExportService = adminExportService;
    }

    @GetMapping
    public String form() {
        return "admin-export";
    }

    @PostMapping
    public ResponseEntity<String> export(@RequestParam(required = false) String usernamePrefix,
                                         Authentication authentication) {
        StringBuilder csv = new StringBuilder("username,displayName,role,verified\n");
        for (User user : adminExportService.exportUsers(usernamePrefix)) {
            csv.append(user.getUsername()).append(',')
                    .append(user.getDisplayName()).append(',')
                    .append(user.getRole()).append(',')
                    .append(user.isVerified()).append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv.toString());
    }
}
