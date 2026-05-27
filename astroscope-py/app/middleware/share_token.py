from app.models import User
from app.platform.federation.tokens import federation_token_verifier


def apply_share_token_from_query():
    from flask import request
    from flask_login import current_user, login_user

    token = request.args.get("shareToken")
    if not token or current_user.is_authenticated:
        return
    username = federation_token_verifier.verify_invite_token(token)
    if not username:
        return
    user = User.query.filter_by(username=username).first()
    if user:
        login_user(user)
