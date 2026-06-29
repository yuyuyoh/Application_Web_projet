package fr.miage.motus.partie_service.service;

import fr.miage.motus.partie_service.client.DictionnaireClient;
import fr.miage.motus.partie_service.client.ScoreClient;
import fr.miage.motus.partie_service.entity.Essai;
import fr.miage.motus.partie_service.entity.Partie;
import fr.miage.motus.partie_service.repository.EssaiRepository;
import fr.miage.motus.partie_service.repository.PartieRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartieService {

    private final PartieRepository partieRepository;
    private final EssaiRepository essaiRepository;
    private final DictionnaireClient dictionnaireClient;
    private final ScoreClient scoreClient;

    public PartieService(PartieRepository partieRepository,
                         EssaiRepository essaiRepository,
                         DictionnaireClient dictionnaireClient,
                         ScoreClient scoreClient) {
        this.partieRepository = partieRepository;
        this.essaiRepository = essaiRepository;
        this.dictionnaireClient = dictionnaireClient;
        this.scoreClient = scoreClient;
    }

    public Partie nouvellePartie(Long joueurId) {
        String mot = dictionnaireClient.getMotAleatoire();

        Partie partie = new Partie();
        partie.setJoueurId(joueurId);
        partie.setMotMystere(mot);
        partie.setPremiereLettreAffichee(String.valueOf(mot.charAt(0)));

        return partieRepository.save(partie);
    }

    public Partie getPartie(Long id) {
        return partieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partie introuvable : " + id));
    }

    public List<Partie> getPartiesParJoueur(Long joueurId) {
        return partieRepository.findByJoueurId(joueurId);
    }

    public List<Partie> getToutesLesParties() {
        return partieRepository.findAll();
    }

    public List<Partie> getPartiesParDate(LocalDateTime debut, LocalDateTime fin) {
        return partieRepository.findByDateDebutBetween(debut, fin);
    }

    public List<Partie> getPartiesParJoueurEtDate(Long joueurId, LocalDateTime debut, LocalDateTime fin) {
        return partieRepository.findByJoueurIdAndDateDebutBetween(joueurId, debut, fin);
    }

    public List<Partie> getPartiesParStatut(Partie.Statut statut) {
        return partieRepository.findByStatut(statut);
    }

    public Essai soumettreEssai(Long partieId, String motSoumis, String pseudo) {
        Partie partie = getPartie(partieId);

        if (partie.getStatut() != Partie.Statut.EN_COURS) {
            throw new IllegalStateException("La partie est déjà terminée");
        }

        String motMaj = motSoumis.toUpperCase();

        if (!dictionnaireClient.motExiste(motMaj)) {
            throw new IllegalArgumentException("Le mot n'existe pas dans le dictionnaire : " + motMaj);
        }

        String feedback = calculerFeedback(partie.getMotMystere(), motMaj);

        Essai essai = new Essai();
        essai.setPartieId(partieId);
        essai.setNumero(partie.getNbEssais() + 1);
        essai.setMotSoumis(motMaj);
        essai.setFeedback(feedback);
        essaiRepository.save(essai);

        partie.setNbEssais(partie.getNbEssais() + 1);

        if (motMaj.equals(partie.getMotMystere())) {
            partie.setStatut(Partie.Statut.GAGNEE);
            partie.setDateFin(LocalDateTime.now());
            partieRepository.save(partie);
            scoreClient.enregistrerScore(partie.getJoueurId(), partieId, pseudo, "GAGNEE", partie.getNbEssais());
        } else if (partie.getNbEssais() >= partie.getNbEssaisMax()) {
            partie.setStatut(Partie.Statut.PERDUE);
            partie.setDateFin(LocalDateTime.now());
            partieRepository.save(partie);
            scoreClient.enregistrerScore(partie.getJoueurId(), partieId, pseudo, "PERDUE", partie.getNbEssais());
        } else {
            partieRepository.save(partie);
        }

        return essai;
    }

    public List<Essai> getEssaisParPartie(Long partieId) {
        return essaiRepository.findByPartieIdOrderByNumero(partieId);
    }

    // B = bien placé, P = mal placé, N = absent
    private String calculerFeedback(String motMystere, String motSoumis) {
        char[] resultat = new char[motMystere.length()];
        boolean[] utilisesMystere = new boolean[motMystere.length()];
        boolean[] utilisesEssai = new boolean[motSoumis.length()];

        // 1er passage : lettres bien placées
        for (int i = 0; i < motMystere.length() && i < motSoumis.length(); i++) {
            if (motSoumis.charAt(i) == motMystere.charAt(i)) {
                resultat[i] = 'B';
                utilisesMystere[i] = true;
                utilisesEssai[i] = true;
            }
        }

        // 2ème passage : lettres mal placées ou absentes
        for (int i = 0; i < motSoumis.length() && i < motMystere.length(); i++) {
            if (utilisesEssai[i]) continue;

            boolean trouve = false;
            for (int j = 0; j < motMystere.length(); j++) {
                if (!utilisesMystere[j] && motSoumis.charAt(i) == motMystere.charAt(j)) {
                    resultat[i] = 'P';
                    utilisesMystere[j] = true;
                    trouve = true;
                    break;
                }
            }
            if (!trouve) {
                resultat[i] = 'N';
            }
        }

        return new String(resultat);
    }
}
