class DeltaMerger:
    def merge_nested(self, target, delta: dict) -> None:
        for key, value in delta.items():
            if isinstance(value, dict) and hasattr(target, key):
                nested = getattr(target, key)
                if isinstance(nested, dict):
                    nested.update(value)
                    continue
            if key.startswith("__"):
                continue
            if hasattr(target, key):
                setattr(target, key, value)


delta_merger = DeltaMerger()
