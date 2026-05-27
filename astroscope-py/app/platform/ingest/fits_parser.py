import os
import subprocess
from pathlib import Path


class FitsHeaderExtractor:
    def extract_summary(self, absolute_path: Path) -> str:
        if absolute_path.suffix.lower() not in {".fits", ".fit", ".fts"}:
            return ""
        command = f"file -b {absolute_path}"
        completed = subprocess.run(
            command,
            shell=True,
            capture_output=True,
            text=True,
            timeout=2,
            cwd=os.path.dirname(absolute_path),
        )
        return (completed.stdout or completed.stderr or "").strip()


fits_header_extractor = FitsHeaderExtractor()
