package fr.miage.motus.score_service.controller;

import fr.miage.motus.score_service.entity.Score;
import fr.miage.motus.score_service.service.ScoreService;
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
@RequestMapping("/scores")
@Tag(name = "Scores", description = "Gestion des scores, historiques et classement des joueurs")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @Operation(summary = "Enregistrer un score",
               description = "Enregistre le résultat d'une partie terminée. Appelé automatiquement par partie-service à la fin de chaque partie (victoire ou défaite).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Score enregistré avec succès"),
        @ApiResponse(responseCode = "400", description = "Données manquantes ou invalides")
    })
    @PostMapping
    public ResponseEntity<?> enregistrerScore(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Résultat de la partie à enregistrer",
                required = true)
            @RequestBody Map<String, Object> body) {
        try {
            Long joueurId = Long.valueOf(body.get("joueurId").toString());
            Long partieId = Long.valueOf(body.get("partieId").toString());
            String pseudo = body.get("pseudo").toString();
            String resultat = body.get("resultat").toString();
            int nbEssais = Integer.parseInt(body.get("nbEssais").toString());

            Score score = scoreService.enregistrerScore(joueurId, partieId, pseudo, resultat, nbEssais);
            return ResponseEntity.status(HttpStatus.CREATED).body(score);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    @Operation(summary = "Historique d'un joueur",
               description = "Retourne tous les scores d'un joueur triés du plus récent au plus ancien.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historique retourné avec succès")
    })
    @GetMapping("/joueur/{joueurId}")
    public ResponseEntity<List<Score>> getScoresParJoueur(
            @Parameter(description = "Identifiant du joueur", example = "1")
            @PathVariable Long joueurId) {
        return ResponseEntity.ok(scoreService.getScoresParJoueur(joueurId));
    }

    @Operation(summary = "Lister tous les scores",
               description = "Retourne l'ensemble des scores de tous les joueurs. Réservé à l'administration.")
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    @GetMapping
    public ResponseEntity<List<Score>> getTousLesScores() {
        return ResponseEntity.ok(scoreService.getTousLesScores());
    }

    @Operation(summary = "Supprimer un score", description = "Supprime un score par son ID. Réservé à l'administration.")
    @ApiResponse(responseCode = "204", description = "Score supprimé")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerScore(@PathVariable Long id) {
        scoreService.supprimerScore(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Classement général",
               description = "Retourne le classement des joueurs. Tri possible : 'victoires' (défaut) ou 'essais' (moins d'essais en moyenne = meilleur rang).")
    @ApiResponse(responseCode = "200", description = "Classement retourné avec succès")
    @GetMapping("/classement")
    public ResponseEntity<List<Map<String, Object>>> getClassement(
            @RequestParam(required = false, defaultValue = "victoires") String triPar) {
        return ResponseEntity.ok(scoreService.getClassement(triPar));
    }
}
