from datetime import datetime

from flask_login import UserMixin
from werkzeug.security import check_password_hash, generate_password_hash

from app.extensions import db


class User(UserMixin, db.Model):
    __tablename__ = "users"

    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(64), unique=True, nullable=False)
    password_hash = db.Column(db.String(256), nullable=False)
    display_name = db.Column(db.String(128), nullable=False)
    institution = db.Column(db.String(256))
    bio = db.Column(db.Text)
    role = db.Column(db.String(32), nullable=False, default="MEMBER")
    verified = db.Column(db.Boolean, default=False)

    def set_password(self, password: str) -> None:
        self.password_hash = generate_password_hash(password)

    def check_password(self, password: str) -> bool:
        return check_password_hash(self.password_hash, password)


class CollaborationGroup(db.Model):
    __tablename__ = "collaboration_groups"

    id = db.Column(db.Integer, primary_key=True)
    slug = db.Column(db.String(64), unique=True, nullable=False)
    name = db.Column(db.String(128), nullable=False)
    description = db.Column(db.Text)
    owner_id = db.Column(db.Integer, db.ForeignKey("users.id"), nullable=False)
    owner = db.relationship("User", foreign_keys=[owner_id])

    members = db.relationship(
        "User",
        secondary="group_members",
        backref="groups",
    )


group_members = db.Table(
    "group_members",
    db.Column("group_id", db.Integer, db.ForeignKey("collaboration_groups.id"), primary_key=True),
    db.Column("user_id", db.Integer, db.ForeignKey("users.id"), primary_key=True),
)


class Observation(db.Model):
    __tablename__ = "observations"

    id = db.Column(db.Integer, primary_key=True)
    author_id = db.Column(db.Integer, db.ForeignKey("users.id"), nullable=False)
    author = db.relationship("User", foreign_keys=[author_id])
    title = db.Column(db.String(256), nullable=False)
    body_markdown = db.Column(db.Text, nullable=False)
    is_private = db.Column(db.Boolean, default=False)
    target_object = db.Column(db.String(128))
    group_id = db.Column(db.Integer, db.ForeignKey("collaboration_groups.id"))
    group = db.relationship("CollaborationGroup")
    created_at = db.Column(db.DateTime, default=datetime.utcnow)


class StarCatalogEntry(db.Model):
    __tablename__ = "star_catalog"

    id = db.Column(db.Integer, primary_key=True)
    designation = db.Column(db.String(64), nullable=False)
    common_name = db.Column(db.String(128), nullable=False)
    constellation = db.Column(db.String(64))
    magnitude = db.Column(db.Float)
    notes = db.Column(db.Text)


class TelescopeImage(db.Model):
    __tablename__ = "telescope_images"

    id = db.Column(db.Integer, primary_key=True)
    owner_id = db.Column(db.Integer, db.ForeignKey("users.id"), nullable=False)
    owner = db.relationship("User")
    original_filename = db.Column(db.String(256), nullable=False)
    stored_relative_path = db.Column(db.String(512), nullable=False)
    caption = db.Column(db.String(512))
    uploaded_at = db.Column(db.DateTime, default=datetime.utcnow)


class PaperShare(db.Model):
    __tablename__ = "paper_shares"

    id = db.Column(db.Integer, primary_key=True)
    owner_id = db.Column(db.Integer, db.ForeignKey("users.id"), nullable=False)
    owner = db.relationship("User")
    title = db.Column(db.String(256), nullable=False)
    abstract_text = db.Column(db.Text, nullable=False)
    share_token = db.Column(db.String(512), unique=True, nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)


class ModeratorDelegation(db.Model):
    __tablename__ = "moderator_delegations"

    id = db.Column(db.Integer, primary_key=True)
    delegator_id = db.Column(db.Integer, db.ForeignKey("users.id"), nullable=False)
    delegate_id = db.Column(db.Integer, db.ForeignKey("users.id"), nullable=False)
    group_id = db.Column(db.Integer, db.ForeignKey("collaboration_groups.id"), nullable=False)
    active = db.Column(db.Boolean, default=True)
    granted_at = db.Column(db.DateTime, default=datetime.utcnow)

    delegate = db.relationship("User", foreign_keys=[delegate_id])
    group = db.relationship("CollaborationGroup")
