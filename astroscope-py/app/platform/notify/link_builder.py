from flask import request


class AbsoluteLinkComposer:
    def compose_share_link(self, path: str) -> str:
        host = request.headers.get("X-Forwarded-Host") or request.host
        scheme = request.headers.get("X-Forwarded-Proto") or request.scheme
        if not path.startswith("/"):
            path = "/" + path
        return f"{scheme}://{host}{path}"


absolute_link_composer = AbsoluteLinkComposer()
