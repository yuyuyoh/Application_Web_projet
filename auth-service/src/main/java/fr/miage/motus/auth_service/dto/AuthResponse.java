package fr.miage.motus.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse d'authentification contenant le token JWT")
public class AuthResponse {

    @Schema(description = "Token JWT à utiliser dans les requêtes suivantes")
    private String token;

    @Schema(description = "Nom d'utilisateur authentifié")
    private String username;

    @Schema(description = "Rôle de l'utilisateur", example = "USER")
    private String role;

    @Schema(description = "Durée de validité en secondes", example = "86400")
    private long expiresIn;

    public AuthResponse(String token, String username, String role, long expiresIn) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public long getExpiresIn() { return expiresIn; }
}
