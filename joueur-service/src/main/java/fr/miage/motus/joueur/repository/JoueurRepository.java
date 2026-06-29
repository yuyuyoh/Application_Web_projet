package fr.miage.motus.joueur.repository;

import fr.miage.motus.joueur.entity.Joueur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JoueurRepository extends JpaRepository<Joueur, Long> {

    Optional<Joueur> findByPseudo(String pseudo);

    boolean existsByPseudo(String pseudo);

    boolean existsByEmail(String email);
}
