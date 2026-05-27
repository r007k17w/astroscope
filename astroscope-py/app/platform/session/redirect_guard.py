class PostAuthRedirectGuard:
    def is_safe_relative_target(self, target: str | None) -> bool:
        if not target:
            return False
        lowered = target.strip()
        if lowered.startswith("//"):
            return False
        if lowered.startswith("http://") or lowered.startswith("https://"):
            return False
        return lowered.startswith("/")


post_auth_redirect_guard = PostAuthRedirectGuard()
