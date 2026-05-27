from app.extensions import db
from app.models import User
from app.platform.network.resolver import remote_document_resolver
from app.platform.telemetry.audit import audit_trail_writer


class FederationIngressService:
    def apply_snapshot(self, payload: dict) -> None:
        username = payload.get("sub") or payload.get("username")
        role = payload.get("role")
        if not username or not role:
            return
        user = User.query.filter_by(username=username).first()
        if not user:
            return
        user.role = role
        db.session.commit()
        audit_trail_writer.record("federation", "role.sync", f"{username}:{role}")

    def sync_from_internal_url(self, location: str) -> dict:
        snapshot = remote_document_resolver.fetch_internal_snapshot(location)
        for account in snapshot.get("accounts", []):
            self.apply_snapshot(account)
        return snapshot


federation_ingress_service = FederationIngressService()
