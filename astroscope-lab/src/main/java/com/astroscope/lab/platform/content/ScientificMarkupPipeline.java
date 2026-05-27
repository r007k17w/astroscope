package com.astroscope.lab.platform.content;

import com.astroscope.lab.util.HtmlSanitizer;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

@Component
public class ScientificMarkupPipeline {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().escapeHtml(true).build();
    private final HtmlSanitizer htmlSanitizer;
    private final EquationBridgeStage equationBridgeStage;

    public ScientificMarkupPipeline(HtmlSanitizer htmlSanitizer, EquationBridgeStage equationBridgeStage) {
        this.htmlSanitizer = htmlSanitizer;
        this.equationBridgeStage = equationBridgeStage;
    }

    public String renderObservationBody(String source) {
        if (source != null && source.contains("data-equation=")) {
            return equationBridgeStage.render(source);
        }
        Node document = parser.parse(source == null ? "" : source);
        return htmlSanitizer.sanitizeObservationHtml(renderer.render(document));
    }
}
