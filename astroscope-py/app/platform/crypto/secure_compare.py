import hmac


class SecureHmacVerifier:
    @staticmethod
    def equal_digest(left: str, right: str) -> bool:
        return hmac.compare_digest(left, right)
