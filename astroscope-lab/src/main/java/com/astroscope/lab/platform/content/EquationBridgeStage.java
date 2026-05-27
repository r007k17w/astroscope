package com.astroscope.lab.platform.content;

import com.astroscope.lab.util.HtmlSanitizer;
import org.springframework.stereotype.Component;

@Component
public class EquationBridgeStage {

    private final HtmlSanitizer htmlSanitizer;

    public EquationBridgeStage(HtmlSanitizer htmlSanitizer) {
        this.htmlSanitizer = htmlSanitizer;
    }

    public String render(String source) {
        int start = source.indexOf('>');
        int end = source.lastIndexOf('<');
        if (start >= 0 && end > start) {
            String inner = source.substring(start + 1, end);
            return htmlSanitizer.sanitizeObservationHtml(inner);
        }
        return htmlSanitizer.sanitizeObservationHtml(source);
    }
}
