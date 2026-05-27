package com.astroscope.lab.platform.access;

import com.astroscope.lab.model.Observation;
import com.astroscope.lab.model.User;

public interface AccessPolicy {
    boolean evaluate(Observation observation, User viewer, AccessRequestContext context);
}
