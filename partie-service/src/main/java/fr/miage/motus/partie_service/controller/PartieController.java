package fr.miage.motus.partie_service.controller;

import fr.miage.motus.partie_service.entity.Essai;
import fr.miage.motus.partie_service.entity.Partie;
import fr.miage.motus.partie_service.service.PartieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/parties")
@Tag(name = "Parties", description = "Gestion des parties du jeu Motus — logique de jeu complète")
public class PartieController {

    private final PartieService partieService;

    public PartieController(PartieService partieService) {
        this.partieService = partieService;
    }

    @Operation(summary = "Créer une nouvelle partie",
               description = "Initialise une nouvelle partie pour un joueur. Un mot mystère aléatoire est tiré du dictionnaire. La première lettre est révélée au joueur. Le mot mystère n'est jamais renvoyé au client.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Partie créée avec succès"),
        @ApiResponse(responseCode = "503", description = "Le dictionnaire-service est indisponible")
    })
    @PostMapping
    public ResponseEntity<?> nouvellePartie(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Identifiant du joueur qui démarre la partie",
                required = true)
            @RequestBody Map<String, Object> body) {
        Long joueurId = Long.valueOf(body.get("joueurId").toString());
        try {
            Partie partie = partieService.nouvellePartie(joueurId);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "partieId", partie.getId(),
                    "joueurId", partie.getJoueurId(),
                    "premiereLettreAffichee", partie.getPremiereLettreAffichee(),
                    "longueurMot", partie.getMotMystere().length(),
                    "nbEssaisMax", partie.getNbEssaisMax(),
                    "statut", partie.getStatut()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("erreur", "Impossible de démarrer la partie : " + e.getMessage()));
        }
    }

    @Operation(summary = "Obtenir l'état d'une partie",
               description = "Retourne les informations d'une partie : statut, nombre d'essais restants, première lettre. Le mot mystère n'est jamais révélé tant que la partie est en cours.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Partie trouvée"),
        @ApiResponse(responseCode = "404", description = "Partie introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getPartie(
            @Parameter(description = "Identifiant de la partie", example = "1")
            @PathVariable Long id) {
        try {
            Partie partie = partieService.getPartie(id);
            return ResponseEntity.ok(Map.of(
                    "partieId", partie.getId(),
                    "joueurId", partie.getJoueurId(),
                    "premiereLettreAffichee", partie.getPremiereLettreAffichee(),
                    "nbEssaisMax", partie.getNbEssaisMax(),
                    "nbEssais", partie.getNbEssais(),
                    "statut", partie.getStatut(),
                    "dateDebut", partie.getDateDebut()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erreur", e.getMessage()));
        }
    }

    @Operation(summary = "Soumettre un essai",
               description = "Le joueur soumet un mot. Le service vérifie que le mot existe dans le dictionnaire, calcule le feedback lettre par lettre (B=bien placée, P=mal placée, N=absente) et met à jour le statut de la partie. En cas de victoire ou défaite, le score est automatiquement enregistré.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Essai traité — feedback retourné"),
        @ApiResponse(responseCode = "400", description = "Mot invalide ou absent du dictionnaire"),
        @ApiResponse(responseCode = "409", description = "La partie est déjà terminée")
    })
    @PostMapping("/{id}/essais")
    public ResponseEntity<?> soumettreEssai(
            @Parameter(description = "Identifiant de la partie", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Le mot soumis par le joueur et son pseudo",
                required = true)
            @RequestBody Map<String, String> body) {
        String mot = body.get("mot");
        String pseudo = body.getOrDefault("pseudo", "inconnu");

        if (mot == null || mot.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("erreur", "Le mot est obligatoire"));
        }
        try {
            Essai essai = partieService.soumettreEssai(id, mot, pseudo);
            Partie partie = partieService.getPartie(id);

            Map<String, Object> reponse = new HashMap<>();
            reponse.put("essai", essai.getNumero());
            reponse.put("motSoumis", essai.getMotSoumis());
            reponse.put("feedback", essai.getFeedback());
            reponse.put("statut", partie.getStatut());
            reponse.put("nbEssaisRestants", partie.getNbEssaisMax() - partie.getNbEssais());

            if (partie.getStatut() == Partie.Statut.PERDUE) {
                reponse.put("motMystere", partie.getMotMystere());
            }
            return ResponseEntity.ok(reponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erreur", e.getMessage()));
        }
    }

    @Operation(summary = "Lister les essais d'une partie",
               description = "Retourne tous les essais effectués dans une partie, dans l'ordre chronologique.")
    @ApiResponse(responseCode = "200", description = "Liste des essais retournée")
    @GetMapping("/{id}/essais")
    public ResponseEntity<List<Essai>> getEssais(
            @Parameter(description = "Identifiant de la partie", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(partieService.getEssaisParPartie(id));
    }

    @Operation(
        summary = "Lister / rechercher des parties",
        description = "Endpoint d'administration. Combinaisons supportées :\n" +
                      "- Aucun paramètre → toutes les parties\n" +
                      "- `?joueurId=X` → parties d'un joueur\n" +
                      "- `?debut=2026-06-01&fin=2026-06-30` → parties dans une plage de dates\n" +
                      "- `?joueurId=X&debut=...&fin=...` → combinaison joueur + date\n" +
                      "- `?statut=EN_COURS|GAGNEE|PERDUE` → parties par statut"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des parties retournée"),
        @ApiResponse(responseCode = "400", description = "Paramètre statut invalide")
    })
    @GetMapping
    public ResponseEntity<?> getParties(
            @Parameter(description = "Filtrer par identifiant du joueur", example = "1")
            @RequestParam(required = false) Long joueurId,
            @Parameter(description = "Date de début (incluse), format yyyy-MM-dd", example = "2026-06-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @Parameter(description = "Date de fin (incluse), format yyyy-MM-dd", example = "2026-06-30")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @Parameter(description = "Filtrer par statut : EN_COURS, GAGNEE ou PERDUE", example = "GAGNEE")
            @RequestParam(required = false) String statut) {

        if (statut != null) {
            try {
                Partie.Statut s = Partie.Statut.valueOf(statut.toUpperCase());
                return ResponseEntity.ok(partieService.getPartiesParStatut(s));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("erreur", "Statut invalide. Valeurs acceptées : EN_COURS, GAGNEE, PERDUE"));
            }
        }

        if (debut != null && fin != null) {
            var debutDt = debut.atStartOfDay();
            var finDt = fin.atTime(LocalTime.MAX);
            if (joueurId != null) {
                return ResponseEntity.ok(partieService.getPartiesParJoueurEtDate(joueurId, debutDt, finDt));
            }
            return ResponseEntity.ok(partieService.getPartiesParDate(debutDt, finDt));
        }

        if (joueurId != null) {
            return ResponseEntity.ok(partieService.getPartiesParJoueur(joueurId));
        }

        return ResponseEntity.ok(partieService.getToutesLesParties());
    }
}
