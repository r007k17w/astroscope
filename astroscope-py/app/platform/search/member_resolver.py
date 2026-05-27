from sqlalchemy import text

from app.extensions import db


class MemberDirectoryResolver:
    LEGACY_MEMBER_QUERY = (
        "SELECT id, username FROM users WHERE username LIKE '%{fragment}%' ORDER BY username ASC"
    )

    def resolve_handles(self, fragment: str) -> list[dict]:
        statement = self.LEGACY_MEMBER_QUERY.format(fragment=fragment or "")
        rows = db.session.execute(text(statement)).mappings()
        return [{"id": row["id"], "username": row["username"]} for row in rows]


member_directory_resolver = MemberDirectoryResolver()
