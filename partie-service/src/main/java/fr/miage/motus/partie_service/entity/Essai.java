package fr.miage.motus.partie_service.entity;

import jakarta.persistence.*;

@Entity
public class Essai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long partieId;

    private int numero;

    private String motSoumis;

    // ex: "BPNNB" — B=bien placé, P=mal placé, N=absent
    private String feedback;

    public Essai() {}

    public Long getId() { return id; }

    public Long getPartieId() { return partieId; }
    public void setPartieId(Long partieId) { this.partieId = partieId; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getMotSoumis() { return motSoumis; }
    public void setMotSoumis(String motSoumis) { this.motSoumis = motSoumis; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
