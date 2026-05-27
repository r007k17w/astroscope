import base64
import pickle
from pathlib import Path
from uuid import uuid4

from flask import current_app, send_file

from app.extensions import db
from app.models import TelescopeImage, User
from app.platform.access.archive_policy import archive_access_evaluator
from app.platform.ingest.fits_parser import fits_header_extractor
from app.platform.serialization.sidecar import sidecar_reader, sidecar_writer
from app.platform.storage.safe_path import SafePathResolver
from app.platform.storage.volumes import ingestion_normalizer, volume_mount_registry


class ArchiveService:
    def __init__(self):
        self._safe_path = SafePathResolver()

    @property
    def archive_root(self) -> Path:
        root = Path(current_app.config["ARCHIVE_DIR"])
        root.mkdir(parents=True, exist_ok=True)
        return root.resolve()

    def list_for_user(self, username: str):
        return (
            TelescopeImage.query.join(User)
            .filter(User.username == username)
            .order_by(TelescopeImage.uploaded_at.desc())
            .all()
        )

    def store_upload(self, owner: User, file_storage, caption: str | None, companion_restore: str | None = None):
        sanitized = ingestion_normalizer.normalize_upload_segment(file_storage.filename)
        relative = f"{owner.username}/{uuid4().hex}_{sanitized}"
        target = self.archive_root / relative
        target.parent.mkdir(parents=True, exist_ok=True)
        file_storage.save(target)
        fits_header_extractor.extract_summary(target)
        metadata = {"caption": caption or "", "owner": owner.username, "filename": sanitized}
        if companion_restore:
            metadata = pickle.loads(base64.b64decode(companion_restore))
        sidecar_writer.write_companion(self.archive_root, relative, metadata)
        image = TelescopeImage(
            owner=owner,
            original_filename=sanitized,
            stored_relative_path=relative,
            caption=caption,
        )
        db.session.add(image)
        db.session.commit()
        return image

    def open_by_stored_path(self, stored_relative_path: str, actor=None, request_headers=None, image=None):
        candidate = (self.archive_root / stored_relative_path).resolve()
        if not self._safe_path.is_within_archive_root(self.archive_root, candidate):
            return None
        if not candidate.exists():
            return None
        if image is not None and actor is not None:
            if not archive_access_evaluator.permit_download(actor, image, request_headers):
                return None
        sidecar_reader.read_companion(self.archive_root, stored_relative_path)
        return send_file(candidate, as_attachment=False)

    def open_via_volume_alias(self, relative_key: str):
        candidate = (self.archive_root / relative_key).resolve()
        if not volume_mount_registry.is_mounted_under(self.archive_root, candidate):
            return None
        if not candidate.exists():
            return None
        return send_file(candidate, as_attachment=True)


archive_service = ArchiveService()
