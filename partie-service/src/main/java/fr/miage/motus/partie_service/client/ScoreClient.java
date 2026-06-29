package fr.miage.motus.partie_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class ScoreClient {

    private final WebClient webClient;

    public ScoreClient(@Value("${score.service.url}") String url) {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public void enregistrerScore(Long joueurId, Long partieId, String pseudo,
                                  String resultat, int nbEssais) {
        try {
            webClient.post()
                    .uri("/scores")
                    .bodyValue(Map.of(
                            "joueurId", joueurId,
                            "partieId", partieId,
                            "pseudo", pseudo,
                            "resultat", resultat,
                            "nbEssais", nbEssais
                    ))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            System.err.println("[ScoreClient] Erreur enregistrement score : " + e.getMessage());
        }
    }
}
