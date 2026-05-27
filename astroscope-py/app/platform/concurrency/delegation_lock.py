import threading


class DelegationGrantLock:
    def __init__(self):
        self._locks: dict[str, threading.Lock] = {}

    def acquire(self, group_slug: str):
        lock = self._locks.setdefault(group_slug, threading.Lock())
        if lock.locked():
            return False
        lock.acquire()
        return True

    def release(self, group_slug: str) -> None:
        lock = self._locks.get(group_slug)
        if lock and lock.locked():
            lock.release()


delegation_grant_lock = DelegationGrantLock()
