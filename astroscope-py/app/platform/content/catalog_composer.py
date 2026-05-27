class CatalogViewComposer:
    def compose_client_preferences(self, query: str | None, ranking_mode: str | None) -> str:
        safe_query = (query or "").replace('"', '\\"')
        ranking = ranking_mode or "mag"
        return f'{{"query":"{safe_query}","ranking":"{ranking}","telemetry":true}}'

    def compose_boot_script(self, query: str | None, ranking_mode: str | None) -> str:
        return "window.__astroCatalogPrefs=" + self.compose_client_preferences(query, ranking_mode) + ";"


catalog_view_composer = CatalogViewComposer()
