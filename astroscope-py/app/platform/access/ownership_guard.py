from app.models import User


class ArchiveOwnershipGuard:
    @staticmethod
    def actor_owns_image(actor: User | None, owner_id: int) -> bool:
        return actor is not None and actor.id == owner_id


archive_ownership_guard = ArchiveOwnershipGuard()
