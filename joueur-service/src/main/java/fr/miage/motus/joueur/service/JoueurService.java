package fr.miage.motus.joueur.service;

import fr.miage.motus.joueur.entity.Joueur;
import fr.miage.motus.joueur.repository.JoueurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JoueurService {

    private final JoueurRepository joueurRepository;

    public JoueurService(JoueurRepository joueurRepository) {
        this.joueurRepository = joueurRepository;
    }

    public Joueur inscrire(String pseudo, String email, String motDePasse) {
        if (joueurRepository.existsByPseudo(pseudo)) {
            throw new IllegalArgumentException("Le pseudo est déjà utilisé : " + pseudo);
        }
        if (joueurRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("L'email est déjà utilisé : " + email);
        }
        Joueur joueur = new Joueur();
        joueur.setPseudo(pseudo);
        joueur.setEmail(email);
        joueur.setMotDePasse(motDePasse);
        return joueurRepository.save(joueur);
    }

    public Joueur getJoueurById(Long id) {
        return joueurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Joueur introuvable : " + id));
    }

    public List<Joueur> getTousLesJoueurs() {
        return joueurRepository.findAll();
    }

    public void supprimerJoueur(Long id) {
        if (!joueurRepository.existsById(id)) {
            throw new RuntimeException("Joueur introuvable : " + id);
        }
        joueurRepository.deleteById(id);
    }
}
