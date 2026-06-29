package fr.miage.motus.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Données pour créer un compte")
public class RegisterRequest {

    @Schema(description = "Nom d'utilisateur unique", example = "joueur1")
    private String username;

    @Schema(description = "Mot de passe (min 6 caractères)", example = "motdepasse123")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
