package com.astroscope.lab.platform.content;

import org.springframework.stereotype.Component;

@Component
public class CatalogViewComposer {

    public String composeClientPreferences(String query, String rankingMode) {
        String safeQuery = query == null ? "" : query.replace("\"", "\\\"");
        return "{\"query\":\"" + safeQuery + "\",\"ranking\":\"" + rankingMode + "\",\"telemetry\":true}";
    }

    public String composeBootScript(String query, String rankingMode) {
        return "window.__astroCatalogPrefs=" + composeClientPreferences(query, rankingMode) + ";";
    }
}
