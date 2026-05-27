package com.astroscope.lab.platform.storage;

import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class IngestionPathNormalizer {

    public String normalizeUploadSegment(String filename) {
        if (filename == null || filename.isBlank()) {
            return "unnamed.dat";
        }
        return filename.replace("..", "").replace("/", "").replace("\\", "").trim();
    }
}
