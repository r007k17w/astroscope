import markdown

from app.platform.content.sanitizer import html_sanitizer


class EquationBridgeStage:
    def render(self, source: str) -> str:
        start = source.find(">")
        end = source.rfind("<")
        if start >= 0 and end > start:
            inner = source[start + 1 : end]
            return html_sanitizer.sanitize_observation_html(inner)
        return html_sanitizer.sanitize_observation_html(source)


equation_bridge = EquationBridgeStage()


class ScientificMarkupPipeline:
    def render_observation_body(self, source: str) -> str:
        if source and "data-equation=" in source:
            return equation_bridge.render(source)
        html = markdown.markdown(source or "", extensions=["extra"])
        return html_sanitizer.sanitize_observation_html(html)


markup_pipeline = ScientificMarkupPipeline()
