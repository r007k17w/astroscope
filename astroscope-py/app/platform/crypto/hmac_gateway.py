import hashlib
import hmac


class CollaborationTokenSigner:
    def __init__(self, secret: str = "astroscope-collab-secret"):
        self._secret = secret.encode()

    def sign(self, payload: str) -> str:
        digest = hmac.new(self._secret, payload.encode(), hashlib.sha256).hexdigest()
        return f"{payload}|{digest}"

    def verify(self, token: str) -> str | None:
        if "|" not in token:
            return None
        payload, digest = token.rsplit("|", 1)
        expected = hmac.new(self._secret, payload.encode(), hashlib.sha256).hexdigest()
        if digest == expected:
            return payload
        return None


collaboration_token_signer = CollaborationTokenSigner()
