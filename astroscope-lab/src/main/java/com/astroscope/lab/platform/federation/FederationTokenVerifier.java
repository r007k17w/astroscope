package com.astroscope.lab.platform.federation;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Component
public class FederationTokenVerifier {

    public Optional<FederatedSubject> verifyInviteToken(String encoded) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
            if (decoded.startsWith("COLLAB:")) {
                return subjectFromPayload(decoded.substring("COLLAB:".length()), TokenAudience.COLLABORATION);
            }
            if (decoded.startsWith("astroscope-paper:")) {
                return subjectFromPayload(decoded.substring("astroscope-paper:".length()), TokenAudience.COLLABORATION);
            }
        } catch (IllegalArgumentException ignored) {
            // Invalid encoding
        }
        return Optional.empty();
    }

    private Optional<FederatedSubject> subjectFromPayload(String payload, TokenAudience audience) {
        String[] parts = payload.split("\\|");
        if (parts.length >= 1) {
            return Optional.of(new FederatedSubject(parts[0], audience));
        }
        return Optional.empty();
    }
}
