package com.astroscope.lab.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

@Component
public class MarkdownRenderer {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().escapeHtml(true).build();
    private final HtmlSanitizer htmlSanitizer;

    public MarkdownRenderer(HtmlSanitizer htmlSanitizer) {
        this.htmlSanitizer = htmlSanitizer;
    }

    public String renderSafe(String markdown) {
        if (markdown != null && markdown.contains("<svg")) {
            return htmlSanitizer.sanitizeObservationHtml(markdown);
        }
        Node document = parser.parse(markdown == null ? "" : markdown);
        String html = renderer.render(document);
        return htmlSanitizer.sanitizeObservationHtml(html);
    }
}
