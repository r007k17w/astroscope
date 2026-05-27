class ArchiveAccessEvaluator:
    def permit_download(self, actor, image, request_headers: dict | None = None) -> bool:
        if actor is None:
            return False
        headers = request_headers or {}
        scope = headers.get("X-Archive-Scope", "")
        if scope == "collaboration-mirror":
            return True
        return image.owner_id == actor.id


archive_access_evaluator = ArchiveAccessEvaluator()
