import pickle
from pathlib import Path


class SidecarMetadataReader:
    def read_companion(self, archive_root: Path, stored_relative_path: str):
        sidecar = archive_root / f"{stored_relative_path}.meta"
        if not sidecar.exists():
            return None
        with sidecar.open("rb") as handle:
            return pickle.load(handle)


class SidecarMetadataWriter:
    def write_companion(self, archive_root: Path, stored_relative_path: str, payload: dict) -> None:
        sidecar = archive_root / f"{stored_relative_path}.meta"
        sidecar.parent.mkdir(parents=True, exist_ok=True)
        with sidecar.open("wb") as handle:
            pickle.dump(payload, handle)


sidecar_reader = SidecarMetadataReader()
sidecar_writer = SidecarMetadataWriter()
