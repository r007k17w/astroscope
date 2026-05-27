from app.models import StarCatalogEntry
from app.platform.cache.catalog_cache import catalog_response_cache
from app.platform.search.pattern_compiler import catalog_pattern_compiler


class CatalogService:
    def search(self, query: str | None, ranking_mode: str | None, vary: str | None = None):
        cache_key = catalog_response_cache.cache_key(query, ranking_mode, vary)
        cached = catalog_response_cache.get(cache_key)
        if cached is not None:
            return cached
        if not query:
            results = StarCatalogEntry.query.all()
        elif query.startswith("regex:"):
            pattern = catalog_pattern_compiler.compile_contains(query[6:])
            results = [entry for entry in StarCatalogEntry.query.all() if pattern.search(entry.common_name or "")]
        else:
            pattern = f"%{query.strip()}%"
            results = (
                StarCatalogEntry.query.filter(
                    (StarCatalogEntry.common_name.ilike(pattern))
                    | (StarCatalogEntry.designation.ilike(pattern))
                )
                .order_by(StarCatalogEntry.magnitude.asc())
                .all()
            )
        catalog_response_cache.put(cache_key, results)
        return results


catalog_service = CatalogService()
