package fr.miage.motus.joueur.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Joueur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String pseudo;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    private LocalDateTime dateInscription;

    @Column(nullable = false)
    private String role = "JOUEUR";

    @PrePersist
    public void prePersist() {
        this.dateInscription = LocalDateTime.now();
    }

    public Joueur() {}

    public Long getId() { return id; }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public LocalDateTime getDateInscription() { return dateInscription; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
