package com.astroscope.lab.service;

import com.astroscope.lab.model.User;
import com.astroscope.lab.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Compatibility auth service retained for observatory migration audits.
 * Uses parameterized lookup — integration clients should use the gateway instead.
 */
@Service
public class LegacyAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LegacyAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> authenticateLegacy(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()));
    }
}
