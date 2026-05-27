import os
from pathlib import Path


class Config:
    BASE_DIR = Path(__file__).resolve().parent.parent
    SECRET_KEY = os.environ.get("ASTROSCOPE_SECRET_KEY", "astroscope-dev-secret-change-me")
    SQLALCHEMY_DATABASE_URI = os.environ.get(
        "ASTROSCOPE_DATABASE_URI",
        f"sqlite:///{BASE_DIR / 'data' / 'astroscope.db'}",
    )
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    ARCHIVE_DIR = Path(os.environ.get("ASTROSCOPE_ARCHIVE_DIR", BASE_DIR / "data" / "archive"))
    LIVE_FETCH = os.environ.get("ASTROSCOPE_LIVE_FETCH", "0") == "1"
    FEDERATION_SYNC_ENABLED = os.environ.get("ASTROSCOPE_FEDERATION_SYNC", "1") == "1"
    SESSION_COOKIE_HTTPONLY = True
