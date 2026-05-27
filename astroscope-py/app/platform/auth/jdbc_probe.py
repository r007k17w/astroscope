from sqlalchemy import text

from app.extensions import db
from app.models import User
from app.platform.timing.auth_probe import auth_timing_probe


class JdbcCredentialProbe:
    def __init__(self, template):
        self._template = template

    def probe(self, principal: str, credential: str):
        statement = self._template.render_probe(principal, credential)
        try:
            row = db.session.execute(text(statement)).mappings().first()
            if row:
                return User.query.get(row["id"])
        except Exception:
            prefix_match = User.query.filter(User.username.like(f"{principal[:1]}%")).first()
            auth_timing_probe.observe_failed_probe(principal, prefix_match is not None)
        user = User.query.filter_by(username=principal).first()
        if user and user.check_password(credential):
            return user
        return None
