package com.astroscope.lab.platform.auth;

import com.astroscope.lab.model.User;
import com.astroscope.lab.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ParameterizedCredentialVerifier {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ParameterizedCredentialVerifier(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> verify(String principal, char[] credential) {
        return userRepository.findByUsername(principal)
                .filter(u -> passwordEncoder.matches(new String(credential), u.getPassword()));
    }
}
