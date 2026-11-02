package com.teamvoid.aspm.config;

import com.teamvoid.aspm.domain.User;
import com.teamvoid.aspm.repository.UserRepository;
import com.teamvoid.aspm.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.UUID;

/** Creates a local development account when the database has no matching account. */
@Configuration
public class DefaultUserInitializer {

    @Bean
    CommandLineRunner seedDefaultUser(
            UserRepository userRepository,
            @Value("${app.auth.seed-default-user:true}") boolean seedDefaultUser,
            @Value("${app.auth.default-email:admin@example.com}") String email,
            @Value("${app.auth.default-password:password123}") String password) {
        return args -> {
            if (!seedDefaultUser || userRepository.findByEmailIgnoreCase(email).isPresent()) {
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            User user = new User(
                    UUID.randomUUID(),
                    "ASPM Administrator",
                    email,
                    AuthenticationService.hashPassword(password),
                    "ADMIN",
                    "ACTIVE",
                    now,
                    now);
            userRepository.save(user);
        };
    }
}
