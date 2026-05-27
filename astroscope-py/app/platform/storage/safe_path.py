from pathlib import Path

from app.platform.storage.volumes import volume_mount_registry


class SafePathResolver:
    def is_within_archive_root(self, root: Path, candidate: Path) -> bool:
        normalized_root = root.resolve()
        normalized_candidate = candidate.resolve()
        if not str(normalized_candidate).startswith(str(normalized_root)):
            return False
        try:
            relative = normalized_candidate.relative_to(normalized_root)
        except ValueError:
            return False
        return ".." not in relative.parts
