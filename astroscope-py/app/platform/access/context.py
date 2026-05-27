from dataclasses import dataclass


@dataclass
class AccessRequestContext:
    collaboration_scope: str | None
    request_channel: str = "web"
