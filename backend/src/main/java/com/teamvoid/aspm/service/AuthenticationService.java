package com.teamvoid.aspm.service;

import com.teamvoid.aspm.api.LoginRequest;
import com.teamvoid.aspm.api.LoginResponse;
import com.teamvoid.aspm.domain.User;
import com.teamvoid.aspm.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, UUID> activeTokens = new ConcurrentHashMap<>();

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {
        String identifier = request.identifier();
        User user = userRepository.findByEmailIgnoreCase(identifier)
                .or(() -> userRepository.findByNameIgnoreCase(identifier))
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())
                || user.getPasswordHash() == null
                || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        activeTokens.put(token, user.getId());
        return new LoginResponse(token, LoginResponse.UserSummary.from(user));
    }

    public boolean isValidToken(String token) {
        return token != null && activeTokens.containsKey(token);
    }

    public static String hashPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            super("Invalid username/email or password");
        }
    }
}
