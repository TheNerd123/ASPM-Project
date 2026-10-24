
package com.teamvoid.aspm;

import com.teamvoid.aspm.domain.User;
import com.teamvoid.aspm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUserStorage() {
        UUID userId = UUID.randomUUID();

        User user = new User(
            userId,
            "AP36 Test User",
            "ap36-" + userId + "@example.com",
            "test-only-hash",
            "STUDENT",
            "ACTIVE"
        );

        userRepository.saveAndFlush(user);

        User savedUser = userRepository.findById(userId)
            .orElseThrow(() -> new AssertionError("User was not found"));

        assertEquals(userId, savedUser.getId());
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getPasswordHash(), savedUser.getPasswordHash());
        assertEquals(user.getRole(), savedUser.getRole());
        assertEquals(user.getStatus(), savedUser.getStatus());

        userRepository.deleteById(userId);
    }
}
