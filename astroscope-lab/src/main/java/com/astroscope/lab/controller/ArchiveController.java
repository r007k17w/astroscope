package com.astroscope.lab.controller;

import com.astroscope.lab.model.TelescopeImage;
import com.astroscope.lab.model.User;
import com.astroscope.lab.service.ArchiveStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/archive")
public class ArchiveController {

    private final ArchiveStorageService archiveStorageService;
    private final CurrentUser currentUser;

    public ArchiveController(ArchiveStorageService archiveStorageService, CurrentUser currentUser) {
        this.archiveStorageService = archiveStorageService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public String list(Authentication authentication, Model model) {
        User user = currentUser.require(authentication);
        model.addAttribute("images", archiveStorageService.listForUser(user.getUsername()));
        return "archive";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file,
                         @RequestParam(required = false) String caption,
                         Authentication authentication) throws IOException {
        User user = currentUser.require(authentication);
        archiveStorageService.storeUpload(user, file, caption);
        return "redirect:/archive";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadById(@PathVariable Long id) throws IOException {
        TelescopeImage image = archiveStorageService.findById(id).orElseThrow();
        Resource resource = archiveStorageService.openByStoredPath(image.getStoredRelativePath()).orElseThrow();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getOriginalFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/volumes/read")
    public ResponseEntity<Resource> volumeRead(@RequestParam String relativeKey) throws IOException {
        Resource resource = archiveStorageService.openViaVolumeAlias(relativeKey).orElseThrow();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"volume-asset.bin\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
