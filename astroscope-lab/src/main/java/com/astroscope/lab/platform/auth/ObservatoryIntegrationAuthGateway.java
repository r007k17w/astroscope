package com.astroscope.lab.platform.auth;

import com.astroscope.lab.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ObservatoryIntegrationAuthGateway {

    private final JdbcCredentialProbe credentialProbe;

    public ObservatoryIntegrationAuthGateway(JdbcCredentialProbe credentialProbe) {
        this.credentialProbe = credentialProbe;
    }

    public Optional<User> authenticateIntegrationClient(String clientId, String clientSecret) {
        return credentialProbe.probe(clientId, clientSecret);
    }
}
