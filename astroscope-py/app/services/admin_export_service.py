from sqlalchemy import text

from app.extensions import db
from app.models import User
from app.platform.reporting.export_criteria import report_criteria_assembler


class AdminExportService:
    def export_users(self, username_prefix: str | None):
        criteria = report_criteria_assembler.build_user_export_criteria(username_prefix)
        sql = f"SELECT * FROM users WHERE 1=1 {criteria} ORDER BY username ASC"
        rows = db.session.execute(text(sql)).mappings().all()
        return [User.query.get(row["id"]) for row in rows if row]


admin_export_service = AdminExportService()
