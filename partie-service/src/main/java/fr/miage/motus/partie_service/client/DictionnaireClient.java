package fr.miage.motus.partie_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class DictionnaireClient {

    private final WebClient webClient;

    public DictionnaireClient(@Value("${dictionnaire.service.url}") String url) {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public String getMotAleatoire() {
        Map response = webClient.get()
                .uri("/mots/aleatoire")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        return (String) response.get("valeur");
    }

    public boolean motExiste(String mot) {
        Map response = webClient.get()
                .uri("/mots/existe/" + mot)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        return (Boolean) response.get("existe");
    }
}
