package fr.miage.motus.auth_service.controller;

import fr.miage.motus.auth_service.dto.AuthResponse;
import fr.miage.motus.auth_service.dto.LoginRequest;
import fr.miage.motus.auth_service.dto.RegisterRequest;
import fr.miage.motus.auth_service.service.AuthService;
import fr.miage.motus.auth_service.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Inscription, connexion et validation de token JWT")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @Operation(
        summary = "Créer un compte",
        description = "Inscrit un nouvel utilisateur et retourne un token JWT valide 24h"
    )
    @ApiResponse(responseCode = "201", description = "Compte créé avec succès")
    @ApiResponse(responseCode = "400", description = "Nom d'utilisateur déjà pris ou données invalides")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    @Operation(
        summary = "Se connecter",
        description = "Authentifie un utilisateur et retourne un token JWT valide 24h"
    )
    @ApiResponse(responseCode = "200", description = "Connexion réussie")
    @ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erreur", "Identifiants incorrects"));
        }
    }

    @Operation(
        summary = "Valider un token JWT",
        description = "Vérifie si le token est valide et non expiré. Retourne le nom d'utilisateur associé."
    )
    @ApiResponse(responseCode = "200", description = "Token valide")
    @ApiResponse(responseCode = "401", description = "Token invalide ou expiré")
    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam String token) {
        if (jwtService.isTokenValid(token)) {
            String username = jwtService.extractUsername(token);
            return ResponseEntity.ok(Map.of(
                    "valide", true,
                    "username", username
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("valide", false, "erreur", "Token invalide ou expiré"));
    }

    @Operation(
        summary = "Statut du service",
        description = "Retourne l'état de santé de l'auth-service"
    )
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(Map.of("statut", "auth-service opérationnel"));
    }
}
