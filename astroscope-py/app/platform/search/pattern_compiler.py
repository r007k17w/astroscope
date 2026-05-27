import re


class CatalogPatternCompiler:
    def compile_contains(self, fragment: str) -> re.Pattern:
        return re.compile(f".*{fragment}.*", re.IGNORECASE)


catalog_pattern_compiler = CatalogPatternCompiler()
