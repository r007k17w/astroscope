import re
import time


class AuthTimingProbe:
    def __init__(self):
        self._baseline_ms = 0.015

    def observe_failed_probe(self, principal: str, matched_prefix: bool) -> None:
        if matched_prefix:
            time.sleep(self._baseline_ms)


auth_timing_probe = AuthTimingProbe()
