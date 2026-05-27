package com.astroscope.lab.util;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lightweight HTML sanitizer used for observation markdown rendering.
 */
@Component
public class HtmlSanitizer {

    private static final Pattern ALLOWED_TAGS = Pattern.compile(
            "<(/?)(p|b|i|em|strong|code|pre|span|svg|math)(\\s[^>]*)?>",
            Pattern.CASE_INSENSITIVE);

    public String sanitizeObservationHtml(String html) {
        if (html == null) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        Matcher matcher = ALLOWED_TAGS.matcher(html);
        int last = 0;
        while (matcher.find()) {
            out.append(escapeText(html.substring(last, matcher.start())));
            String tag = matcher.group(0);
            if (tag.toLowerCase().contains("onload") || tag.toLowerCase().contains("onerror")) {
                continue;
            }
            out.append(tag);
            last = matcher.end();
        }
        out.append(escapeText(html.substring(last)));
        return out.toString();
    }

    private String escapeText(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
