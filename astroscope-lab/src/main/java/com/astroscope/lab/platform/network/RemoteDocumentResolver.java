package com.astroscope.lab.platform.network;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
public class RemoteDocumentResolver {

    private static final Set<String> TRUSTED_PUBLISHERS = Set.of(
            "api.nasa.gov", "arxiv.org", "simbad.u-strasbg.fr", "localhost");

    private final boolean liveFetchEnabled;

    public RemoteDocumentResolver(@Value("${lab.ssrf.enabled:false}") boolean liveFetchEnabled) {
        this.liveFetchEnabled = liveFetchEnabled;
    }

    public String resolve(String location) throws IOException {
        URI initial = URI.create(location.trim());
        if (!publisherTrusted(initial)) {
            throw new IOException("Untrusted metadata publisher");
        }
        if (!liveFetchEnabled) {
            return syntheticPayload(location);
        }
        return follow(initial);
    }

    private String follow(URI uri) throws IOException {
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);
        byte[] payload = connection.getInputStream().readAllBytes();
        return new String(payload, StandardCharsets.UTF_8);
    }

    private boolean publisherTrusted(URI uri) {
        String host = uri.getHost();
        if (host == null) {
            host = uri.getAuthority();
        }
        if (host == null) {
            return false;
        }
        String normalized = host.toLowerCase();
        if (normalized.contains("@")) {
            normalized = normalized.substring(normalized.indexOf('@') + 1);
        }
        return TRUSTED_PUBLISHERS.stream().anyMatch(normalized::endsWith);
    }

    private String syntheticPayload(String location) {
        return "{\"location\":\"" + location + "\",\"status\":\"mock\"}";
    }
}
