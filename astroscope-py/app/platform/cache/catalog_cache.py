class CatalogResponseCache:
    def __init__(self):
        self._entries: dict[str, object] = {}

    def cache_key(self, query: str | None, ranking: str | None, vary: str | None) -> str:
        return f"{query or ''}|{ranking or 'mag'}|{vary or ''}"

    def get(self, key: str):
        return self._entries.get(key)

    def put(self, key: str, payload) -> None:
        self._entries[key] = payload


catalog_response_cache = CatalogResponseCache()
