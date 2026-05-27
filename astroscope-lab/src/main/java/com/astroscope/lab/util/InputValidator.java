package com.astroscope.lab.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class InputValidator {

    private static final Pattern USERNAME = Pattern.compile("^[a-zA-Z0-9_.-]{3,32}$");

    public boolean isValidUsername(String username) {
        return username != null && USERNAME.matcher(username).matches();
    }

    public String sanitizeForDisplay(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
