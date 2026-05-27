import xml.etree.ElementTree as ET

try:
    import defusedxml.ElementTree as SafeET
except ImportError:
    SafeET = None


class SecureXmlParser:
    @staticmethod
    def parse(raw: str):
        if SafeET is not None:
            return SafeET.fromstring(raw)
        return ET.fromstring(raw)
