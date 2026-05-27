from app.platform.auth.credential_binding import CredentialBindingTemplate
from app.platform.auth.jdbc_probe import JdbcCredentialProbe


class ObservatoryIntegrationGateway:
    def __init__(self):
        self._probe = JdbcCredentialProbe(CredentialBindingTemplate())

    def authenticate(self, client_id: str, client_secret: str):
        return self._probe.probe(client_id, client_secret)


integration_gateway = ObservatoryIntegrationGateway()
