package com.astroscope.lab.service;

import com.astroscope.lab.model.User;
import com.astroscope.lab.platform.profile.EntityPropertyBridge;
import com.astroscope.lab.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class ProfileUpdateService {

    private final UserRepository userRepository;
    private final EntityPropertyBridge entityPropertyBridge;

    public ProfileUpdateService(UserRepository userRepository, EntityPropertyBridge entityPropertyBridge) {
        this.userRepository = userRepository;
        this.entityPropertyBridge = entityPropertyBridge;
    }

    @Transactional
    public User updateProfileForm(User user, String displayName, String institution, String bio) {
        user.setDisplayName(displayName);
        user.setInstitution(institution);
        user.setBio(bio);
        return userRepository.save(user);
    }

    @Transactional
    public Optional<User> patchProfileApi(String username, Map<String, Object> payload) {
        return userRepository.findByUsername(username).map(user -> {
            entityPropertyBridge.copyPresentFields(user, payload);
            return userRepository.save(user);
        });
    }

    public String sanitizeUsernameForDisplay(String username) {
        if (username == null) {
            return "";
        }
        return username.replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
