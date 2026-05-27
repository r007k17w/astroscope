package com.astroscope.lab.util;

import org.springframework.stereotype.Component;

/**
 * Parameterized export filter builder used by modern reporting flows.
 */
@Component
public class SecureQueryBuilder {

    public String buildUserExportFilter(String usernamePrefix) {
        if (usernamePrefix == null || usernamePrefix.isBlank()) {
            return "";
        }
        String escaped = usernamePrefix.replace("'", "''");
        return " AND username LIKE '" + escaped + "%' ESCAPE '!' ";
    }
}
