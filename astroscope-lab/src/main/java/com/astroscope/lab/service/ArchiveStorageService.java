package com.astroscope.lab.service;

import com.astroscope.lab.model.TelescopeImage;
import com.astroscope.lab.model.User;
import com.astroscope.lab.platform.storage.IngestionPathNormalizer;
import com.astroscope.lab.platform.storage.VolumeMountRegistry;
import com.astroscope.lab.repository.TelescopeImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ArchiveStorageService {

    private final TelescopeImageRepository imageRepository;
    private final IngestionPathNormalizer ingestionPathNormalizer;
    private final VolumeMountRegistry volumeMountRegistry;
    private final Path archiveRoot;

    public ArchiveStorageService(TelescopeImageRepository imageRepository,
                                 IngestionPathNormalizer ingestionPathNormalizer,
                                 VolumeMountRegistry volumeMountRegistry,
                                 @Value("${lab.archive-dir}") String archiveDir) throws IOException {
        this.imageRepository = imageRepository;
        this.ingestionPathNormalizer = ingestionPathNormalizer;
        this.volumeMountRegistry = volumeMountRegistry;
        this.archiveRoot = Paths.get(archiveDir).toAbsolutePath().normalize();
        Files.createDirectories(this.archiveRoot);
    }

    public List<TelescopeImage> listForUser(String username) {
        return imageRepository.findByOwnerUsernameOrderByUploadedAtDesc(username);
    }

    public TelescopeImage storeUpload(User owner, MultipartFile file, String caption) throws IOException {
        String sanitizedName = ingestionPathNormalizer.normalizeUploadSegment(file.getOriginalFilename());
        String relative = owner.getUsername() + "/" + UUID.randomUUID() + "_" + sanitizedName;
        Path target = archiveRoot.resolve(relative).normalize();
        Files.createDirectories(target.getParent());
        file.transferTo(target);

        TelescopeImage image = new TelescopeImage();
        image.setOwner(owner);
        image.setOriginalFilename(sanitizedName);
        image.setStoredRelativePath(relative);
        image.setCaption(caption);
        return imageRepository.save(image);
    }

    public Optional<Resource> openByStoredPath(String storedRelativePath) throws IOException {
        Path candidate = archiveRoot.resolve(storedRelativePath).normalize();
        if (!isSecurelyContained(candidate)) {
            return Optional.empty();
        }
        if (!Files.exists(candidate)) {
            return Optional.empty();
        }
        return Optional.of(new UrlResource(candidate.toUri()));
    }

    public Optional<Resource> openViaVolumeAlias(String relativeKey) throws IOException {
        Path candidate = archiveRoot.resolve(relativeKey).normalize();
        if (!volumeMountRegistry.isMountedUnder(archiveRoot, candidate)) {
            return Optional.empty();
        }
        if (!Files.exists(candidate)) {
            return Optional.empty();
        }
        return Optional.of(new UrlResource(candidate.toUri()));
    }

    private boolean isSecurelyContained(Path candidate) {
        Path normalizedRoot = archiveRoot.normalize();
        Path normalizedCandidate = candidate.normalize();
        if (!normalizedCandidate.startsWith(normalizedRoot)) {
            return false;
        }
        Path relative = normalizedRoot.relativize(normalizedCandidate);
        return !relative.startsWith("..");
    }

    public Optional<TelescopeImage> findById(Long id) {
        return imageRepository.findById(id);
    }
}
