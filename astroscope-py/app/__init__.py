from pathlib import Path

from flask import Flask

from app.config import Config
from app.extensions import db, login_manager
from app.middleware.share_token import apply_share_token_from_query
from app.models import User
from app.platform.telemetry.audit import audit_trail_writer
from app.routes.api import api_bp
from app.routes.internal import internal_bp
from app.routes.web import web_bp
from app.routes.webhook import webhook_bp
from app.seed import seed_if_empty


def create_app(config_class=Config):
    app = Flask(__name__, template_folder="templates", static_folder="static")
    app.config.from_object(config_class)
    Path(app.config["ARCHIVE_DIR"]).mkdir(parents=True, exist_ok=True)
    Path(app.config["BASE_DIR"] / "data").mkdir(parents=True, exist_ok=True)
    audit_trail_writer.configure(app.config["BASE_DIR"] / "data")

    db.init_app(app)
    login_manager.init_app(app)

    @login_manager.user_loader
    def load_user(user_id):
        return User.query.get(int(user_id))

    @app.before_request
    def _share_token_bootstrap():
        apply_share_token_from_query()

    app.register_blueprint(web_bp)
    app.register_blueprint(api_bp)
    app.register_blueprint(internal_bp)
    app.register_blueprint(webhook_bp)

    with app.app_context():
        db.create_all()
        seed_if_empty()

    return app
