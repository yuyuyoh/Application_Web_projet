package fr.miage.motus.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SwaggerProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${dictionnaire.service.url:http://localhost:8082}")
    private String dictionnaireUrl;

    @Value("${joueur.service.url:http://localhost:8081}")
    private String joueurUrl;

    @Value("${partie.service.url:http://localhost:8083}")
    private String partieUrl;

    @Value("${score.service.url:http://localhost:8084}")
    private String scoreUrl;

    @Value("${auth.service.url:http://localhost:8085}")
    private String authUrl;

    @GetMapping(value = "/proxy/dictionnaire/api-docs", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getDictionnaireApiDocs() {
        return rewrite(fetchDocs(dictionnaireUrl + "/v3/api-docs", "dictionnaire-service"), dictionnaireUrl, "http://localhost:8082");
    }

    @GetMapping(value = "/proxy/joueur/api-docs", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getJoueurApiDocs() {
        return rewrite(fetchDocs(joueurUrl + "/v3/api-docs", "joueur-service"), joueurUrl, "http://localhost:8081");
    }

    @GetMapping(value = "/proxy/partie/api-docs", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPartieApiDocs() {
        return rewrite(fetchDocs(partieUrl + "/v3/api-docs", "partie-service"), partieUrl, "http://localhost:8083");
    }

    @GetMapping(value = "/proxy/score/api-docs", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getScoreApiDocs() {
        return rewrite(fetchDocs(scoreUrl + "/v3/api-docs", "score-service"), scoreUrl, "http://localhost:8084");
    }

    @GetMapping(value = "/proxy/auth/api-docs", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAuthApiDocs() {
        return rewrite(fetchDocs(authUrl + "/v3/api-docs", "auth-service"), authUrl, "http://localhost:8085");
    }

    private String fetchDocs(String url, String serviceName) {
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return "{\"openapi\":\"3.0.1\",\"info\":{\"title\":\"" + serviceName
                    + "\",\"description\":\"Service non disponible.\",\"version\":\"N/A\"},\"paths\":{}}";
        }
    }

    // Remplace l'URL interne Docker (ex: http://joueur-service:8081) par localhost
    private String rewrite(String json, String internalUrl, String externalUrl) {
        if (json == null) return "";
        return json.replace(internalUrl, externalUrl);
    }
}
