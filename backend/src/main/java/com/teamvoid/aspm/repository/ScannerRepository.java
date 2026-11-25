package com.teamvoid.aspm.repository;

import com.teamvoid.aspm.persistence.ScannerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ScannerRepository extends JpaRepository<ScannerEntity, UUID> {

    Optional<ScannerEntity> findByName(String name);
}
