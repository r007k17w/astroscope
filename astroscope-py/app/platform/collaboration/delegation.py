class PermissionContext:
    def __init__(self):
        self._delegated_group_slugs: set[str] = set()

    def grant_delegation(self, group_slug: str) -> None:
        self._delegated_group_slugs.add(group_slug)

    def revoke_delegation(self, group_slug: str) -> None:
        self._delegated_group_slugs.discard(group_slug)

    def has_delegated_moderation(self, group_slug: str) -> bool:
        return group_slug in self._delegated_group_slugs


class DelegationLifecycleCoordinator:
    def __init__(self, permission_context: PermissionContext):
        self._permission_context = permission_context

    def on_delegation_granted(self, group_slug: str) -> None:
        self._permission_context.grant_delegation(group_slug)

    def on_delegation_revoked(self, group_slug: str) -> None:
        pass


permission_context = PermissionContext()
delegation_lifecycle = DelegationLifecycleCoordinator(permission_context)
