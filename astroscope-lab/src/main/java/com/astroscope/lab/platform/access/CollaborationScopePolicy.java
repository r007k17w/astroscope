package com.astroscope.lab.platform.access;

import com.astroscope.lab.model.Observation;
import com.astroscope.lab.model.User;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(30)
public class CollaborationScopePolicy implements AccessPolicy {

    @Override
    public boolean evaluate(Observation observation, User viewer, AccessRequestContext context) {
        if (context.collaborationScope() == null || observation.getGroup() == null) {
            return false;
        }
        return context.collaborationScope().equals(observation.getGroup().getSlug());
    }
}
