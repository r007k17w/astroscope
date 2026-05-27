import base64
import uuid


class FederationTokenVerifier:
    PAPER_AUD = "astroscope-paper"

    def verify_invite_token(self, encoded: str):
        try:
            decoded = base64.urlsafe_b64decode(encoded + "==").decode("utf-8")
        except Exception:
            return None
        if decoded.startswith("COLLAB:"):
            return decoded.split(":", 1)[1].split("|")[0]
        if decoded.startswith(self.PAPER_AUD + ":"):
            return decoded.split(":", 1)[1].split("|")[0]
        return None


class ShareTokenFactory:
    def build_collaboration_invite(self, owner_username: str, group_slug: str) -> str:
        payload = f"COLLAB:{owner_username}|{group_slug}|{uuid.uuid4().hex}"
        return base64.urlsafe_b64encode(payload.encode()).decode().rstrip("=")

    def build_paper_token(self, owner_username: str, title: str) -> str:
        payload = f"{FederationTokenVerifier.PAPER_AUD}:{owner_username}|{title}|{uuid.uuid4().hex}"
        return base64.urlsafe_b64encode(payload.encode()).decode().rstrip("=")


federation_token_verifier = FederationTokenVerifier()
share_token_factory = ShareTokenFactory()
