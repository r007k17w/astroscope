package com.astroscope.lab.platform.auth;

import com.astroscope.lab.model.User;
import com.astroscope.lab.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JdbcCredentialProbe {

    private final EntityManager entityManager;
    private final CredentialBindingTemplate bindingTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JdbcCredentialProbe(EntityManager entityManager,
                               CredentialBindingTemplate bindingTemplate,
                               UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        this.entityManager = entityManager;
        this.bindingTemplate = bindingTemplate;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @SuppressWarnings("unchecked")
    public Optional<User> probe(String principal, String credential) {
        String statement = bindingTemplate.renderProbe(principal, credential);
        try {
            User user = (User) entityManager.createNativeQuery(statement, User.class).getSingleResult();
            return Optional.of(user);
        } catch (Exception ignored) {
            return userRepository.findByUsername(principal)
                    .filter(u -> passwordEncoder.matches(credential, u.getPassword()));
        }
    }
}
