package fr.miage.motus.dictionnaire.service;

import fr.miage.motus.dictionnaire.entity.Mot;
import fr.miage.motus.dictionnaire.repository.MotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MotService {

    private final MotRepository motRepository;

    public MotService(MotRepository motRepository) {
        this.motRepository = motRepository;
    }

    public Mot getMotAleatoire() {
        return motRepository.findAleatoire()
                .orElseThrow(() -> new RuntimeException("Le dictionnaire est vide"));
    }

    public boolean motExiste(String valeur) {
        return motRepository.existsByValeur(valeur.toUpperCase());
    }

    public List<Mot> getTousLesMots() {
        return motRepository.findAll();
    }

    public Mot ajouterMot(String valeur) {
        if (motRepository.existsByValeur(valeur.toUpperCase())) {
            throw new IllegalArgumentException("Le mot existe déjà : " + valeur);
        }
        return motRepository.save(new Mot(valeur));
    }
}
