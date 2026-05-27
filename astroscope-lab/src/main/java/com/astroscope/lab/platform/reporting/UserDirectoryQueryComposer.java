package com.astroscope.lab.platform.reporting;

import org.springframework.stereotype.Component;

@Component
public class UserDirectoryQueryComposer {

    public String composeUsernamePrefixClause(String normalizedPrefix) {
        if (normalizedPrefix == null || normalizedPrefix.isBlank()) {
            return "";
        }
        return " AND username LIKE '" + normalizedPrefix + "%' ";
    }
}
