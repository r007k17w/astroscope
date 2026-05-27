package com.astroscope.lab.service;

import com.astroscope.lab.platform.network.RemoteDocumentResolver;
import org.springframework.stereotype.Component;

@Component
public class ExternalFetchClient {

    private final RemoteDocumentResolver remoteDocumentResolver;

    public ExternalFetchClient(RemoteDocumentResolver remoteDocumentResolver) {
        this.remoteDocumentResolver = remoteDocumentResolver;
    }

    public String fetchMetadata(String rawUrl) throws Exception {
        return remoteDocumentResolver.resolve(rawUrl);
    }
}
