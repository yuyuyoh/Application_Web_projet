package fr.miage.motus.partie_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Partie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long joueurId;

    private String motMystere;

    private String premiereLettreAffichee;

    private int nbEssaisMax = 6;

    private int nbEssais = 0;

    @Enumerated(EnumType.STRING)
    private Statut statut = Statut.EN_COURS;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    @PrePersist
    public void prePersist() {
        this.dateDebut = LocalDateTime.now();
    }

    public enum Statut {
        EN_COURS, GAGNEE, PERDUE
    }

    public Partie() {}

    public Long getId() { return id; }

    public Long getJoueurId() { return joueurId; }
    public void setJoueurId(Long joueurId) { this.joueurId = joueurId; }

    public String getMotMystere() { return motMystere; }
    public void setMotMystere(String motMystere) { this.motMystere = motMystere; }

    public String getPremiereLettreAffichee() { return premiereLettreAffichee; }
    public void setPremiereLettreAffichee(String premiereLettreAffichee) { this.premiereLettreAffichee = premiereLettreAffichee; }

    public int getNbEssaisMax() { return nbEssaisMax; }
    public void setNbEssaisMax(int nbEssaisMax) { this.nbEssaisMax = nbEssaisMax; }

    public int getNbEssais() { return nbEssais; }
    public void setNbEssais(int nbEssais) { this.nbEssais = nbEssais; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public LocalDateTime getDateDebut() { return dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
}
