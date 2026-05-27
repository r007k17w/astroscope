from app.extensions import db
from app.models import User
from app.platform.profile.bridge import entity_property_bridge


class ProfileService:
    def update_profile_form(self, user, display_name, institution, bio):
        user.display_name = display_name
        user.institution = institution
        user.bio = bio
        db.session.commit()
        return user

    def patch_profile_api(self, user, payload: dict):
        entity_property_bridge.copy_present_fields(user, payload)
        db.session.commit()
        return user

    def sanitize_username_for_display(self, username: str) -> str:
        return (
            (username or "")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace('"', "&quot;")
        )


profile_service = ProfileService()
