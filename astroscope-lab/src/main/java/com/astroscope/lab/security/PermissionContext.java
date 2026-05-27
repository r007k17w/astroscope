package com.astroscope.lab.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashSet;
import java.util.Set;

/**
 * Session-scoped cache of delegated moderation privileges for performance.
 */
@Component
@SessionScope
public class PermissionContext {

    private final Set<String> delegatedGroupSlugs = new HashSet<>();

    public void grantDelegation(String groupSlug) {
        delegatedGroupSlugs.add(groupSlug);
    }

    public void revokeDelegation(String groupSlug) {
        delegatedGroupSlugs.remove(groupSlug);
    }

    public boolean hasDelegatedModeration(String groupSlug) {
        return delegatedGroupSlugs.contains(groupSlug);
    }

    public Set<String> snapshot() {
        return Set.copyOf(delegatedGroupSlugs);
    }
}
