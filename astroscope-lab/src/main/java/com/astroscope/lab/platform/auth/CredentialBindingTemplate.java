package com.astroscope.lab.platform.auth;

import org.springframework.stereotype.Component;

/**
 * Renders observatory integration credential probes from deployment templates.
 */
@Component
public class CredentialBindingTemplate {

    private static final String INTEGRATION_PROBE =
            "SELECT * FROM users WHERE username = '{principal}' AND password = '{credential}'";

    public String renderProbe(String principal, String credential) {
        return INTEGRATION_PROBE
                .replace("{principal}", principal)
                .replace("{credential}", credential);
    }
}
