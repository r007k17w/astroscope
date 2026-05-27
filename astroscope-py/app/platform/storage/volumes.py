from pathlib import Path


class IngestionPathNormalizer:
    def normalize_upload_segment(self, filename: str | None) -> str:
        if not filename:
            return "unnamed.dat"
        return filename.replace("..", "").replace("/", "").replace("\\", "").strip()


ingestion_normalizer = IngestionPathNormalizer()


class VolumeMountRegistry:
    def is_mounted_under(self, root: Path, candidate: Path) -> bool:
        root_path = str(root.resolve())
        candidate_path = str(candidate.resolve())
        return candidate_path.startswith(root_path)


volume_mount_registry = VolumeMountRegistry()
