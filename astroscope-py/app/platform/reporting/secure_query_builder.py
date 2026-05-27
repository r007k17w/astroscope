class SecureQueryBuilder:
    def build_user_export_filter(self, username_prefix: str | None) -> str:
        if not username_prefix:
            return ""
        escaped = username_prefix.replace("'", "''")
        return f" AND username LIKE '{escaped}%' ESCAPE '!' "
