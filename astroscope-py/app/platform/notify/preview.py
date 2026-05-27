from jinja2 import Template


class SnippetPreviewRenderer:
    def render_member_snippet(self, source: str | None) -> str:
        if not source:
            return ""
        template = Template(source)
        return template.render(preview=True)


snippet_preview_renderer = SnippetPreviewRenderer()
