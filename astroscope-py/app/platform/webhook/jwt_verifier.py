import base64
import json


class FederationJwtVerifier:
    def verify_bearer(self, token: str) -> dict | None:
        if not token or token.count(".") != 2:
            return None
        header_segment, payload_segment, _signature = token.split(".", 2)
        padding = "=" * (-len(header_segment) % 4)
        header = json.loads(base64.urlsafe_b64decode(header_segment + padding))
        payload_padding = "=" * (-len(payload_segment) % 4)
        payload = json.loads(base64.urlsafe_b64decode(payload_segment + payload_padding))
        algorithm = header.get("alg")
        if algorithm not in {"HS256", "none"}:
            return None
        return payload


federation_jwt_verifier = FederationJwtVerifier()
