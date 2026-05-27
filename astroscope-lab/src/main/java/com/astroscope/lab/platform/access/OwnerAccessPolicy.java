package com.astroscope.lab.platform.access;

import com.astroscope.lab.model.Observation;
import com.astroscope.lab.model.User;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class OwnerAccessPolicy implements AccessPolicy {

    @Override
    public boolean evaluate(Observation observation, User viewer, AccessRequestContext context) {
        return viewer != null && observation.getAuthor().getUsername().equals(viewer.getUsername());
    }
}
