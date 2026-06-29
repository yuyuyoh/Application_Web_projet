package fr.miage.motus.dictionnaire.controller;

import fr.miage.motus.dictionnaire.entity.Mot;
import fr.miage.motus.dictionnaire.service.MotService;
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
@RequestMapping("/mots")
@Tag(name = "Dictionnaire", description = "Gestion du dictionnaire de mots pour le jeu Motus")
public class MotController {

    private final MotService motService;

    public MotController(MotService motService) {
        this.motService = motService;
    }

    @Operation(summary = "Obtenir un mot aléatoire",
               description = "Retourne un mot aléatoire issu du dictionnaire. Utilisé par partie-service pour initialiser une nouvelle partie.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mot retourné avec succès"),
        @ApiResponse(responseCode = "500", description = "Le dictionnaire est vide")
    })
    @GetMapping("/aleatoire")
    public ResponseEntity<Mot> getMotAleatoire() {
        return ResponseEntity.ok(motService.getMotAleatoire());
    }

    @Operation(summary = "Vérifier si un mot existe",
               description = "Vérifie si le mot soumis par le joueur existe dans le dictionnaire. Retourne true ou false.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vérification effectuée")
    })
    @GetMapping("/existe/{valeur}")
    public ResponseEntity<Map<String, Boolean>> motExiste(
            @Parameter(description = "Le mot à vérifier", example = "CHIEN")
            @PathVariable String valeur) {
        boolean existe = motService.motExiste(valeur);
        return ResponseEntity.ok(Map.of("existe", existe));
    }

    @Operation(summary = "Lister tous les mots",
               description = "Retourne la liste complète des mots du dictionnaire. Réservé à l'administration.")
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    @GetMapping
    public ResponseEntity<List<Mot>> getTousLesMots() {
        return ResponseEntity.ok(motService.getTousLesMots());
    }

    @Operation(summary = "Ajouter un mot",
               description = "Ajoute un nouveau mot dans le dictionnaire. Réservé à l'administration.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Mot ajouté avec succès"),
        @ApiResponse(responseCode = "400", description = "Le champ valeur est manquant"),
        @ApiResponse(responseCode = "409", description = "Le mot existe déjà dans le dictionnaire")
    })
    @PostMapping
    public ResponseEntity<Mot> ajouterMot(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Le mot à ajouter",
                required = true)
            @RequestBody Map<String, String> body) {
        String valeur = body.get("valeur");
        if (valeur == null || valeur.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Mot mot = motService.ajouterMot(valeur);
        return ResponseEntity.status(HttpStatus.CREATED).body(mot);
    }
}
