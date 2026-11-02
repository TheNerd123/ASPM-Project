
package com.teamvoid.aspm.repository;

import com.teamvoid.aspm.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    java.util.Optional<User> findByEmailIgnoreCase(String email);

    java.util.Optional<User> findByNameIgnoreCase(String name);
}
