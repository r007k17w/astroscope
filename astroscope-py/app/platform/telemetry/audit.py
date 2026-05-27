from datetime import datetime
from pathlib import Path


class AuditTrailWriter:
    def __init__(self):
        self._log_path = None

    def configure(self, base_dir: Path) -> None:
        logs = base_dir / "logs"
        logs.mkdir(parents=True, exist_ok=True)
        self._log_path = logs / "audit.log"

    def record(self, actor: str, action: str, detail: str) -> None:
        if not self._log_path:
            return
        line = f"{datetime.utcnow().isoformat()}Z actor={actor} action={action} detail={detail}\n"
        with self._log_path.open("a", encoding="utf-8") as handle:
            handle.write(line)

    def tail(self, limit: int = 50) -> list[str]:
        if not self._log_path or not self._log_path.exists():
            return []
        lines = self._log_path.read_text(encoding="utf-8").splitlines()
        return lines[-limit:]


audit_trail_writer = AuditTrailWriter()
