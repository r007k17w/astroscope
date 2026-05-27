from app.models import User


class InternalMetricsSnapshot:
    def build_payload(self) -> dict:
        users = User.query.order_by(User.username.asc()).all()
        return {
            "active_accounts": len(users),
            "accounts": [
                {
                    "username": user.username,
                    "role": user.role,
                    "verified": user.verified,
                }
                for user in users
            ],
        }


internal_metrics_snapshot = InternalMetricsSnapshot()
