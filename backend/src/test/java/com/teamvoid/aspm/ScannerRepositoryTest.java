package com.teamvoid.aspm;

import com.teamvoid.aspm.persistence.ScannerEntity;
import com.teamvoid.aspm.repository.ScannerRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import jakarta.persistence.EntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class ScannerRepositoryTest {

    @Autowired
    private ScannerRepository scannerRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void canCreateAndRetrieveScanner() {
        UUID id = UUID.randomUUID();

        ScannerEntity scanner = new ScannerEntity(
                id,
                "Test Scanner",
                "SAST",
                true,
                "{\"config\":\"auto\"}",
                Instant.now()
        );

        scannerRepository.saveAndFlush(scanner);
        entityManager.clear();

        ScannerEntity retrieved = scannerRepository.findById(id).orElseThrow();

        assertEquals(id, retrieved.getId());
        assertEquals("Test Scanner", retrieved.getName());
        assertEquals("SAST", retrieved.getType());
        assertTrue(retrieved.isEnabled());
        assertEquals("{\"config\":\"auto\"}", retrieved.getConfiguration());
        assertNotNull(retrieved.getCreatedAt());
    }

    @Test
    void canUpdateScannerConfiguration() {
        UUID id = UUID.randomUUID();

        ScannerEntity scanner = new ScannerEntity(
                id,
                "Configuration Test Scanner",
                "SAST",
                true,
                "{\"config\":\"auto\"}",
                Instant.now()
        );

        scannerRepository.saveAndFlush(scanner);
        entityManager.clear();

        ScannerEntity existing = scannerRepository.findById(id).orElseThrow();

        existing.setConfiguration("{\"config\":\"p/default\"}");

        scannerRepository.saveAndFlush(existing);
        entityManager.clear();

        ScannerEntity updated = scannerRepository.findById(id).orElseThrow();

        assertEquals("{\"config\":\"p/default\"}", updated.getConfiguration());
    }

    @Test
    void canUpdateScannerStatus() {
        UUID id = UUID.randomUUID();

        ScannerEntity scanner = new ScannerEntity(
                id,
                "Status Test Scanner",
                "SAST",
                true,
                "{\"config\":\"auto\"}",
                Instant.now()
        );

        scannerRepository.saveAndFlush(scanner);
        entityManager.clear();

        ScannerEntity existing = scannerRepository.findById(id).orElseThrow();

        existing.setEnabled(false);

        scannerRepository.saveAndFlush(existing);
        entityManager.clear();

        ScannerEntity updated = scannerRepository.findById(id).orElseThrow();

        assertFalse(updated.isEnabled());

        updated.setEnabled(true);

        scannerRepository.saveAndFlush(updated);
        entityManager.clear();

        ScannerEntity reenabled = scannerRepository.findById(id).orElseThrow();

        assertTrue(reenabled.isEnabled());
    }
}