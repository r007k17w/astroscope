import re

ALLOWED_TAG = re.compile(
    r"<(/?)(p|b|i|em|strong|code|pre|span|svg|math)(\s[^>]*)?>",
    re.IGNORECASE,
)


class HtmlSanitizer:
    def sanitize_observation_html(self, html: str) -> str:
        if not html:
            return ""
        out = []
        last = 0
        for match in ALLOWED_TAG.finditer(html):
            out.append(self._escape_text(html[last : match.start()]))
            tag = match.group(0)
            lowered = tag.lower()
            if "onload" in lowered or "onerror" in lowered:
                last = match.end()
                continue
            out.append(tag)
            last = match.end()
        out.append(self._escape_text(html[last:]))
        return "".join(out)

    def _escape_text(self, text: str) -> str:
        return (
            text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        )


html_sanitizer = HtmlSanitizer()
