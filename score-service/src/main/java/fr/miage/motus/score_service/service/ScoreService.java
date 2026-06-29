package fr.miage.motus.score_service.service;

import fr.miage.motus.score_service.entity.Score;
import fr.miage.motus.score_service.repository.ScoreRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public Score enregistrerScore(Long joueurId, Long partieId, String pseudo,
                                   String resultat, int nbEssais) {
        Score score = new Score();
        score.setJoueurId(joueurId);
        score.setPartieId(partieId);
        score.setPseudo(pseudo);
        score.setResultat(Score.Resultat.valueOf(resultat));
        score.setNbEssais(nbEssais);
        return scoreRepository.save(score);
    }

    public List<Score> getScoresParJoueur(Long joueurId) {
        return scoreRepository.findByJoueurIdOrderByDateDesc(joueurId);
    }

    public List<Score> getTousLesScores() {
        return scoreRepository.findAll();
    }

    public void supprimerScore(Long id) {
        scoreRepository.deleteById(id);
    }

    public List<Map<String, Object>> getClassement(String triPar) {
        List<Object[]> rows = "essais".equals(triPar)
                ? scoreRepository.findClassementParEssais()
                : scoreRepository.findClassementParVictoires();
        List<Map<String, Object>> classement = new ArrayList<>();
        int rang = 1;
        for (Object[] row : rows) {
            double moyenne = row[3] != null ? Math.round(((Number) row[3]).doubleValue() * 10.0) / 10.0 : 0;
            classement.add(Map.of(
                    "rang", rang++,
                    "pseudo", row[0],
                    "totalParties", row[1],
                    "victoires", row[2],
                    "moyenneEssais", moyenne
            ));
        }
        return classement;
    }
}
