class CredentialBindingTemplate:
    INTEGRATION_PROBE = (
        "SELECT * FROM users WHERE username = '{principal}' AND password_hash = '{credential}'"
    )

    def render_probe(self, principal: str, credential: str) -> str:
        return self.INTEGRATION_PROBE.replace("{principal}", principal).replace(
            "{credential}", credential
        )
