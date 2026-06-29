package fr.miage.motus.joueur.controller;

import fr.miage.motus.joueur.entity.Joueur;
import fr.miage.motus.joueur.service.JoueurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/joueurs")
@Tag(name = "Joueurs", description = "Gestion des joueurs du jeu Motus")
public class JoueurController {

    private final JoueurService joueurService;

    public JoueurController(JoueurService joueurService) {
        this.joueurService = joueurService;
    }

    @Operation(summary = "Inscrire un joueur",
               description = "Crée un nouveau compte joueur avec un pseudo unique, un email et un mot de passe.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Joueur inscrit avec succès"),
        @ApiResponse(responseCode = "400", description = "Champs obligatoires manquants"),
        @ApiResponse(responseCode = "409", description = "Pseudo ou email déjà utilisé")
    })
    @PostMapping
    public ResponseEntity<?> inscrire(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Informations du joueur à inscrire",
                required = true)
            @RequestBody Map<String, String> body) {
        String pseudo = body.get("pseudo");
        String email = body.get("email");
        String motDePasse = body.get("motDePasse");

        if (pseudo == null || email == null || motDePasse == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erreur", "pseudo, email et motDePasse sont obligatoires"));
        }
        try {
            Joueur joueur = joueurService.inscrire(pseudo, email, motDePasse);
            return ResponseEntity.status(HttpStatus.CREATED).body(joueur);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    @Operation(summary = "Obtenir le profil d'un joueur",
               description = "Retourne les informations d'un joueur à partir de son identifiant.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Joueur trouvé"),
        @ApiResponse(responseCode = "404", description = "Joueur introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getJoueur(
            @Parameter(description = "Identifiant du joueur", example = "1")
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(joueurService.getJoueurById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    @Operation(summary = "Lister tous les joueurs",
               description = "Retourne la liste de tous les joueurs inscrits. Réservé à l'administration.")
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    @GetMapping
    public ResponseEntity<List<Joueur>> getTousLesJoueurs() {
        return ResponseEntity.ok(joueurService.getTousLesJoueurs());
    }

    @Operation(summary = "Supprimer un joueur",
               description = "Supprime le compte d'un joueur à partir de son identifiant. Réservé à l'administration.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Joueur supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Joueur introuvable")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerJoueur(
            @Parameter(description = "Identifiant du joueur à supprimer", example = "1")
            @PathVariable Long id) {
        try {
            joueurService.supprimerJoueur(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }
}
