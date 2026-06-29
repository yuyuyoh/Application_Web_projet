package fr.miage.motus.score_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long joueurId;

    private Long partieId;

    private String pseudo;

    @Enumerated(EnumType.STRING)
    private Resultat resultat;

    private int nbEssais;

    private LocalDateTime date;

    @PrePersist
    public void prePersist() {
        this.date = LocalDateTime.now();
    }

    public enum Resultat {
        GAGNEE, PERDUE
    }

    public Score() {}

    public Long getId() { return id; }

    public Long getJoueurId() { return joueurId; }
    public void setJoueurId(Long joueurId) { this.joueurId = joueurId; }

    public Long getPartieId() { return partieId; }
    public void setPartieId(Long partieId) { this.partieId = partieId; }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public Resultat getResultat() { return resultat; }
    public void setResultat(Resultat resultat) { this.resultat = resultat; }

    public int getNbEssais() { return nbEssais; }
    public void setNbEssais(int nbEssais) { this.nbEssais = nbEssais; }

    public LocalDateTime getDate() { return date; }
}
