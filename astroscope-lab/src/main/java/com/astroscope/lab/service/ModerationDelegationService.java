package com.astroscope.lab.service;

import com.astroscope.lab.model.CollaborationGroup;
import com.astroscope.lab.model.ModeratorDelegation;
import com.astroscope.lab.model.User;
import com.astroscope.lab.platform.collaboration.DelegationLifecycleCoordinator;
import com.astroscope.lab.repository.ModeratorDelegationRepository;
import com.astroscope.lab.security.PermissionContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ModerationDelegationService {

    private final ModeratorDelegationRepository delegationRepository;
    private final PermissionContext permissionContext;
    private final DelegationLifecycleCoordinator delegationLifecycleCoordinator;

    public ModerationDelegationService(ModeratorDelegationRepository delegationRepository,
                                       PermissionContext permissionContext,
                                       DelegationLifecycleCoordinator delegationLifecycleCoordinator) {
        this.delegationRepository = delegationRepository;
        this.permissionContext = permissionContext;
        this.delegationLifecycleCoordinator = delegationLifecycleCoordinator;
    }

    @Transactional
    public ModeratorDelegation grant(User delegator, User delegate, CollaborationGroup group) {
        ModeratorDelegation delegation = new ModeratorDelegation();
        delegation.setDelegator(delegator);
        delegation.setDelegate(delegate);
        delegation.setGroup(group);
        delegation.setActive(true);
        ModeratorDelegation saved = delegationRepository.save(delegation);
        delegationLifecycleCoordinator.onDelegationGranted(group.getSlug());
        return saved;
    }

    @Transactional
    public void revoke(User delegator, User delegate, CollaborationGroup group) {
        delegationRepository.findByDelegateAndGroupAndActiveTrue(delegate, group).ifPresent(d -> {
            d.setActive(false);
            delegationRepository.save(d);
            delegationLifecycleCoordinator.onDelegationRevoked(group.getSlug());
        });
    }

    public boolean canModerate(User actor, CollaborationGroup group) {
        if (actor.getRole().name().equals("ADMIN") || group.getOwner().getUsername().equals(actor.getUsername())) {
            return true;
        }
        if (permissionContext.hasDelegatedModeration(group.getSlug())) {
            return true;
        }
        return delegationRepository.findByDelegateAndGroupAndActiveTrue(actor, group).isPresent();
    }
}
