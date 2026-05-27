from app.models import User
from app.platform.merge.delta_merger import delta_merger


BLOCKED = {"id", "username", "password_hash"}


class EntityPropertyBridge:
    def copy_present_fields(self, target: User, delta: dict) -> None:
        if "attributes" in delta and isinstance(delta["attributes"], dict):
            delta_merger.merge_nested(target, delta["attributes"])
        for key, value in delta.items():
            if key in BLOCKED or key == "attributes" or not hasattr(target, key):
                continue
            setattr(target, key, value)


entity_property_bridge = EntityPropertyBridge()
