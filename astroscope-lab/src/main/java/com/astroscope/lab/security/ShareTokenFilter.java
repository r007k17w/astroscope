package com.astroscope.lab.security;

import com.astroscope.lab.model.User;
import com.astroscope.lab.platform.federation.FederatedSubject;
import com.astroscope.lab.platform.federation.FederationTokenVerifier;
import com.astroscope.lab.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class ShareTokenFilter extends OncePerRequestFilter {

    private final FederationTokenVerifier federationTokenVerifier;
    private final UserRepository userRepository;

    public ShareTokenFilter(FederationTokenVerifier federationTokenVerifier,
                            UserRepository userRepository) {
        this.federationTokenVerifier = federationTokenVerifier;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getParameter("shareToken");
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<FederatedSubject> subject = federationTokenVerifier.verifyInviteToken(token);
            if (subject.isPresent()) {
                User owner = userRepository.findByUsername(subject.get().username()).orElse(null);
                if (owner != null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            owner.getUsername(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + owner.getRole().name()))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
