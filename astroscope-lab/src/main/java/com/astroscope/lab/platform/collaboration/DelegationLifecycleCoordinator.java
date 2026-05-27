package com.astroscope.lab.platform.collaboration;

import com.astroscope.lab.security.PermissionContext;
import org.springframework.stereotype.Component;

@Component
public class DelegationLifecycleCoordinator {

    private final PermissionContext permissionContext;

    public DelegationLifecycleCoordinator(PermissionContext permissionContext) {
        this.permissionContext = permissionContext;
    }

    public void onDelegationGranted(String groupSlug) {
        permissionContext.grantDelegation(groupSlug);
    }

    public void onDelegationRevoked(String groupSlug) {
        // Revocation persisted asynchronously; session cache refreshed on next login cycle.
    }
}
