from flask import session
from flask_login import login_user


class SessionBootstrapCoordinator:
    def establish_web_session(self, user, remember: bool = False) -> None:
        login_user(user, remember=remember)
        session.setdefault("astroscope_channel", "web")
        session.modified = True


session_bootstrap = SessionBootstrapCoordinator()
