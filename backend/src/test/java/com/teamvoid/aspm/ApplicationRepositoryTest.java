
package com.teamvoid.aspm;

import com.teamvoid.aspm.domain.AssetStatus;
import com.teamvoid.aspm.persistence.ApplicationEntity;
import com.teamvoid.aspm.repository.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApplicationRepositoryTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    void testApplicationStorage() {
        UUID applicationId = UUID.randomUUID();

        ApplicationEntity application = new ApplicationEntity(
            applicationId,
            "AP68 Test Application",
            "https://github.com/teamvoid/test-app",
            AssetStatus.ACTIVE,
            Instant.now()
        );

        try {
            // Test saving an application
            applicationRepository.saveAndFlush(application);

            // Test retrieving an application
            ApplicationEntity saved = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new AssertionError("Application was not found"));

            assertEquals(applicationId, saved.getId());
            assertEquals(application.getName(), saved.getName());
            assertEquals(application.getRepositoryUrl(), saved.getRepositoryUrl());
            assertEquals(AssetStatus.ACTIVE, saved.getStatus());
            assertNotNull(saved.getCreatedAt());

            // Test updating an application
            saved.setName("AP68 Updated Application");
            applicationRepository.saveAndFlush(saved);

            ApplicationEntity updated = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new AssertionError("Updated application was not found"));

            assertEquals("AP68 Updated Application", updated.getName());

            // Test deleting an application
            applicationRepository.deleteById(applicationId);
            applicationRepository.flush();

            assertFalse(applicationRepository.existsById(applicationId));

        } finally {
            // Clean up test data if an assertion fails
            applicationRepository.deleteById(applicationId);
        }
    }
}
