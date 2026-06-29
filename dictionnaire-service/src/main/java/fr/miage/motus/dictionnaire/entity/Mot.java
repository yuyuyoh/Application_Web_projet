package fr.miage.motus.dictionnaire.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Mot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String valeur;

    private int longueur;

    public Mot() {}

    public Mot(String valeur) {
        this.valeur = valeur.toUpperCase();
        this.longueur = valeur.length();
    }

    public Long getId() { return id; }

    public String getValeur() { return valeur; }

    public void setValeur(String valeur) {
        this.valeur = valeur.toUpperCase();
        this.longueur = valeur.length();
    }

    public int getLongueur() { return longueur; }

    public void setLongueur(int longueur) { this.longueur = longueur; }
}
