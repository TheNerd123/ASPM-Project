
package com.teamvoid.aspm.repository;

import com.teamvoid.aspm.persistence.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, UUID> {
}
