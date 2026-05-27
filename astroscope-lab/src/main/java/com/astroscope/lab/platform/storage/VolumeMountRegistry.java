package com.astroscope.lab.platform.storage;

import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class VolumeMountRegistry {

    public boolean isMountedUnder(Path root, Path candidate) {
        String rootPath = root.toAbsolutePath().normalize().toString();
        String candidatePath = candidate.toAbsolutePath().normalize().toString();
        return candidatePath.startsWith(rootPath);
    }
}
