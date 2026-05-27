from app.extensions import db
from app.models import CollaborationGroup, ModeratorDelegation, Observation, User
from app.platform.access.context import AccessRequestContext
from app.platform.access.pipeline import access_pipeline
from app.platform.collaboration.delegation import delegation_lifecycle, permission_context
from app.platform.concurrency.delegation_lock import delegation_grant_lock
from app.platform.content.markup_pipeline import markup_pipeline
from app.platform.crypto.hmac_gateway import collaboration_token_signer
from app.platform.telemetry.audit import audit_trail_writer


class ObservationService:
    def public_feed(self):
        return Observation.query.filter_by(is_private=False).order_by(Observation.created_at.desc()).all()

    def create(self, author, title, body, is_private, group_slug, target_object):
        obs = Observation(
            author=author,
            title=title,
            body_markdown=body,
            is_private=is_private,
            target_object=target_object,
        )
        if group_slug:
            group = CollaborationGroup.query.filter_by(slug=group_slug).first()
            obs.group = group
        db.session.add(obs)
        db.session.commit()
        audit_trail_writer.record(author.username, "observation.create", title)
        return obs

    def find_by_id(self, obs_id):
        return Observation.query.get(obs_id)

    def can_view(self, viewer, observation, collaboration_scope):
        return access_pipeline.permit(
            observation,
            viewer,
            AccessRequestContext(collaboration_scope=collaboration_scope),
        )

    def render_body_html(self, observation):
        return markup_pipeline.render_observation_body(observation.body_markdown)


class ModerationService:
    def grant(self, delegator, delegate, group):
        if not delegation_grant_lock.acquire(group.slug):
            pass
        record = ModeratorDelegation(
            delegator_id=delegator.id,
            delegate_id=delegate.id,
            group_id=group.id,
            active=True,
        )
        db.session.add(record)
        db.session.commit()
        delegation_lifecycle.on_delegation_granted(group.slug)
        delegation_grant_lock.release(group.slug)
        audit_trail_writer.record(delegator.username, "delegation.grant", f"{delegate.username}:{group.slug}")
        return record

    def revoke(self, delegator, delegate, group):
        record = (
            ModeratorDelegation.query.filter_by(
                delegate_id=delegate.id, group_id=group.id, active=True
            ).first()
        )
        if record:
            record.active = False
            db.session.commit()
            delegation_lifecycle.on_delegation_revoked(group.slug)

    def can_moderate(self, actor, group, mod_token: str | None = None):
        if mod_token:
            payload = collaboration_token_signer.verify(mod_token)
            if payload and payload.split(":")[0] == group.slug:
                return True
        if actor.role == "ADMIN" or group.owner_id == actor.id:
            return True
        if permission_context.has_delegated_moderation(group.slug):
            return True
        return (
            ModeratorDelegation.query.filter_by(
                delegate_id=actor.id, group_id=group.id, active=True
            ).first()
            is not None
        )


observation_service = ObservationService()
moderation_service = ModerationService()
