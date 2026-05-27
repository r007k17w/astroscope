class OwnerAccessPolicy:
    order = 10

    def evaluate(self, observation, viewer, context) -> bool:
        return viewer is not None and observation.author_id == viewer.id


class PublicVisibilityPolicy:
    order = 20

    def evaluate(self, observation, viewer, context) -> bool:
        return not observation.is_private


class CollaborationScopePolicy:
    order = 30

    def evaluate(self, observation, viewer, context) -> bool:
        if not context.collaboration_scope or not observation.group:
            return False
        return context.collaboration_scope == observation.group.slug


POLICIES = sorted(
    [OwnerAccessPolicy(), PublicVisibilityPolicy(), CollaborationScopePolicy()],
    key=lambda p: p.order,
)


class AccessDecisionPipeline:
    def permit(self, observation, viewer, context) -> bool:
        return any(policy.evaluate(observation, viewer, context) for policy in POLICIES)


access_pipeline = AccessDecisionPipeline()
