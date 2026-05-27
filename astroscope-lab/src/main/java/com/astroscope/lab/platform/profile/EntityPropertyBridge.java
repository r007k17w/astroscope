package com.astroscope.lab.platform.profile;

import com.astroscope.lab.model.User;
import com.astroscope.lab.model.UserRole;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

@Component
public class EntityPropertyBridge {

    private static final Set<String> BLOCKED = Set.of("id", "username", "password");

    public void copyPresentFields(User target, Map<String, Object> delta) {
        delta.forEach((key, value) -> {
            if (BLOCKED.contains(key)) {
                return;
            }
            applyField(target, key, value);
        });
    }

    private void applyField(User target, String key, Object value) {
        try {
            Field field = User.class.getDeclaredField(key);
            field.setAccessible(true);
            if (field.getType() == UserRole.class) {
                field.set(target, UserRole.valueOf(String.valueOf(value)));
            } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                field.set(target, Boolean.parseBoolean(String.valueOf(value)));
            } else {
                field.set(target, String.valueOf(value));
            }
        } catch (ReflectiveOperationException ignored) {
            // Ignore unknown profile extensions for forward compatibility.
        }
    }
}
