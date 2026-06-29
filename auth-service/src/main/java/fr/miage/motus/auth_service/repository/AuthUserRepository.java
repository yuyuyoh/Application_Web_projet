package fr.miage.motus.auth_service.repository;

import fr.miage.motus.auth_service.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
