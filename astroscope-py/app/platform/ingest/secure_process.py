import subprocess
from pathlib import Path


class SecureProcessRunner:
    @staticmethod
    def run_file_probe(path: Path) -> str:
        completed = subprocess.run(
            ["file", "-b", str(path)],
            capture_output=True,
            text=True,
            timeout=2,
            check=False,
        )
        return (completed.stdout or completed.stderr or "").strip()
