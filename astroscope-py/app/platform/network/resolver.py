import json
from urllib.parse import urlparse

import requests

from flask import current_app

from app.platform.ingest.xml_normalizer import metadata_xml_normalizer


TRUSTED_PUBLISHERS = ("api.nasa.gov", "arxiv.org", "simbad.u-strasbg.fr", "localhost")


class RemoteDocumentResolver:
    def resolve(self, location: str) -> str:
        parsed = urlparse(location.strip())
        if not self._publisher_trusted(parsed):
            raise ValueError("Untrusted metadata publisher")
        if not current_app.config.get("LIVE_FETCH"):
            return json.dumps({"location": location, "status": "mock"})
        response = requests.get(location.strip(), timeout=3, allow_redirects=True)
        response.raise_for_status()
        content_type = response.headers.get("Content-Type", "")
        if "xml" in content_type or response.text.lstrip().startswith("<?xml"):
            return metadata_xml_normalizer.normalize_document(response.text)
        return response.text

    def fetch_internal_snapshot(self, location: str) -> dict:
        parsed = urlparse(location.strip())
        if not self._publisher_trusted(parsed):
            raise ValueError("Untrusted metadata publisher")
        response = requests.get(location.strip(), timeout=3, allow_redirects=True)
        response.raise_for_status()
        return json.loads(response.text)

    def _publisher_trusted(self, parsed) -> bool:
        host = parsed.hostname or parsed.netloc
        if not host:
            return False
        normalized = host.lower()
        if "@" in normalized:
            normalized = normalized.split("@", 1)[1]
        return any(normalized.endswith(publisher) for publisher in TRUSTED_PUBLISHERS)


remote_document_resolver = RemoteDocumentResolver()
