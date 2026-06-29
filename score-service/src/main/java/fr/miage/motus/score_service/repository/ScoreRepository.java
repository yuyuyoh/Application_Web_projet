package fr.miage.motus.score_service.repository;

import fr.miage.motus.score_service.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findByJoueurIdOrderByDateDesc(Long joueurId);

    List<Score> findByDateBetween(LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT s.pseudo, COUNT(s) as totalParties, " +
           "SUM(CASE WHEN s.resultat = 'GAGNEE' THEN 1 ELSE 0 END) as victoires, " +
           "AVG(CASE WHEN s.resultat = 'GAGNEE' THEN s.nbEssais ELSE NULL END) as moyenneEssais " +
           "FROM Score s GROUP BY s.joueurId, s.pseudo ORDER BY victoires DESC, moyenneEssais ASC")
    List<Object[]> findClassementParVictoires();

    @Query("SELECT s.pseudo, COUNT(s) as totalParties, " +
           "SUM(CASE WHEN s.resultat = 'GAGNEE' THEN 1 ELSE 0 END) as victoires, " +
           "AVG(CASE WHEN s.resultat = 'GAGNEE' THEN s.nbEssais ELSE NULL END) as moyenneEssais " +
           "FROM Score s GROUP BY s.joueurId, s.pseudo " +
           "HAVING SUM(CASE WHEN s.resultat = 'GAGNEE' THEN 1 ELSE 0 END) > 0 " +
           "ORDER BY moyenneEssais ASC, victoires DESC")
    List<Object[]> findClassementParEssais();
}
