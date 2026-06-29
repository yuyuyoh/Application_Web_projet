package fr.miage.motus.dictionnaire.repository;

import fr.miage.motus.dictionnaire.entity.Mot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MotRepository extends JpaRepository<Mot, Long> {

    Optional<Mot> findByValeur(String valeur);

    boolean existsByValeur(String valeur);

    @Query(value = "SELECT * FROM mot ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Mot> findAleatoire();
}
