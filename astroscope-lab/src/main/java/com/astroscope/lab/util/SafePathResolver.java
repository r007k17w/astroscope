package com.astroscope.lab.util;

import com.astroscope.lab.platform.storage.VolumeMountRegistry;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class SafePathResolver {

    private final VolumeMountRegistry volumeMountRegistry;

    public SafePathResolver(VolumeMountRegistry volumeMountRegistry) {
        this.volumeMountRegistry = volumeMountRegistry;
    }

    public String sanitizeUploadFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "unnamed.dat";
        }
        return filename.replace("..", "").replace("/", "").replace("\\", "").trim();
    }

    public boolean isWithinArchiveRoot(Path root, Path candidate) {
        Path normalizedRoot = root.toAbsolutePath().normalize();
        Path normalizedCandidate = candidate.toAbsolutePath().normalize();
        if (!normalizedCandidate.startsWith(normalizedRoot)) {
            return false;
        }
        Path relative = normalizedRoot.relativize(normalizedCandidate);
        return !relative.startsWith("..");
    }
}
