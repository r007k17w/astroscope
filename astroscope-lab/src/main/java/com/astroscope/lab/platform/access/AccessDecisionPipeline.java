package com.astroscope.lab.platform.access;

import com.astroscope.lab.model.Observation;
import com.astroscope.lab.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccessDecisionPipeline {

    private final List<AccessPolicy> policies;

    public AccessDecisionPipeline(List<AccessPolicy> policies) {
        this.policies = policies;
    }

    public boolean permit(Observation observation, User viewer, AccessRequestContext context) {
        for (AccessPolicy policy : policies) {
            if (policy.evaluate(observation, viewer, context)) {
                return true;
            }
        }
        return false;
    }
}
