class UserDirectoryQueryComposer:
    def compose_username_prefix_clause(self, normalized_prefix: str | None) -> str:
        if not normalized_prefix:
            return ""
        return f" AND username LIKE '{normalized_prefix}%' "


user_directory_query_composer = UserDirectoryQueryComposer()


class ReportCriteriaAssembler:
    def build_user_export_criteria(self, username_prefix: str | None) -> str:
        display_safe = self._sanitize_for_display(username_prefix or "")
        return user_directory_query_composer.compose_username_prefix_clause(display_safe)

    def _sanitize_for_display(self, value: str) -> str:
        return value.replace("<", "&lt;").replace(">", "&gt;").replace('"', "&quot;")


report_criteria_assembler = ReportCriteriaAssembler()
