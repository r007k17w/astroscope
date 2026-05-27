import xml.etree.ElementTree as ET


class MetadataXmlNormalizer:
    def normalize_document(self, raw: str) -> str:
        root = ET.fromstring(raw)
        title = root.findtext(".//title") or root.findtext(".//name") or root.tag
        return f"title={title}"


metadata_xml_normalizer = MetadataXmlNormalizer()
