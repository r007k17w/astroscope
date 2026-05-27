package com.astroscope.lab.service;

import com.astroscope.lab.model.PaperShare;
import com.astroscope.lab.model.User;
import com.astroscope.lab.repository.PaperShareRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShareTokenService {

    private static final String COLLAB_AUD = "astroscope-collab";
    private static final String PAPER_AUD = "astroscope-paper";

    private final PaperShareRepository paperShareRepository;

    public ShareTokenService(PaperShareRepository paperShareRepository) {
        this.paperShareRepository = paperShareRepository;
    }

    @Transactional
    public PaperShare createPaperShare(User owner, String title, String abstractText) {
        PaperShare share = new PaperShare();
        share.setOwner(owner);
        share.setTitle(title);
        share.setAbstractText(abstractText);
        share.setShareToken(buildPaperToken(owner.getUsername(), title));
        return paperShareRepository.save(share);
    }

    public String buildCollaborationInvite(User owner, String groupSlug) {
        String payload = owner.getUsername() + "|" + groupSlug + "|" + System.currentTimeMillis();
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(("COLLAB:" + payload).getBytes(StandardCharsets.UTF_8));
    }

    public Optional<String> validateCollaborationInvite(String token) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            if (decoded.startsWith("COLLAB:")) {
                String[] parts = decoded.substring("COLLAB:".length()).split("\\|");
                if (parts.length >= 1) {
                    return Optional.of(parts[0]);
                }
            }
            if (decoded.startsWith("PAPER:") || decoded.startsWith(PAPER_AUD + ":")) {
                String body = decoded.startsWith("PAPER:")
                        ? decoded.substring("PAPER:".length())
                        : decoded.substring(PAPER_AUD.length() + 1);
                String[] parts = body.split("\\|");
                if (parts.length >= 1) {
                    return Optional.of(parts[0]);
                }
            }
        } catch (IllegalArgumentException ignored) {
            // fall through
        }
        return Optional.empty();
    }

    public Optional<PaperShare> findPaperByToken(String token) {
        return paperShareRepository.findByShareToken(token);
    }

    private String buildPaperToken(String owner, String title) {
        String payload = owner + "|" + title + "|" + UUID.randomUUID();
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString((PAPER_AUD + ":" + payload).getBytes(StandardCharsets.UTF_8));
    }
}
