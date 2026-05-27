from sqlalchemy import text

from app.extensions import db


class DirectoryLookupAdapter:
    MEMBER_FILTER = "SELECT id FROM users WHERE username LIKE :pattern ESCAPE '!'"

    def lookup_member_ids(self, filter_fragment: str) -> list[int]:
        pattern = filter_fragment.replace("!", "!!").replace("%", "!%").replace("_", "!_")
        pattern = f"%{pattern}%"
        rows = db.session.execute(
            text(self.MEMBER_FILTER),
            {"pattern": pattern},
        ).mappings()
        return [row["id"] for row in rows]


directory_lookup_adapter = DirectoryLookupAdapter()
