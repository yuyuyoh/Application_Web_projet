package fr.miage.motus.partie_service.repository;

import fr.miage.motus.partie_service.entity.Partie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PartieRepository extends JpaRepository<Partie, Long> {

    List<Partie> findByJoueurId(Long joueurId);

    List<Partie> findByDateDebutBetween(LocalDateTime debut, LocalDateTime fin);

    List<Partie> findByJoueurIdAndDateDebutBetween(Long joueurId, LocalDateTime debut, LocalDateTime fin);

    List<Partie> findByStatut(Partie.Statut statut);
}
