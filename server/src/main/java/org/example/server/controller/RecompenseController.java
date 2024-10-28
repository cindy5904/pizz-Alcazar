package org.example.server.controller;

import org.example.server.dto.recompense.RecompenseDtoGet;
import org.example.server.dto.recompense.RecompenseDtoPost;
import org.example.server.service.RecompenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recompenses")
public class RecompenseController {
    @Autowired
    private RecompenseService recompenseService;

    /**
     * Créer une nouvelle récompense.
     * @param dto le DTO de la récompense à créer.
     * @return la récompense créée.
     */
    @PostMapping
    public ResponseEntity<RecompenseDtoGet> createRecompense(@RequestBody RecompenseDtoPost dto) {
        RecompenseDtoGet recompenseDto = recompenseService.createRecompense(dto);
        return ResponseEntity.ok(recompenseDto);
    }

    /**
     * Générer une récompense pour un utilisateur.
     * @param utilisateurId l'ID de l'utilisateur.
     * @return une réponse vide.
     */
    @PostMapping("/generer/{utilisateurId}")
    public ResponseEntity<Void> genererRecompensePourUtilisateur(@PathVariable Long utilisateurId) {
        recompenseService.genererRecompensePourUtilisateur(utilisateurId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupérer l'historique des récompenses d'un utilisateur.
     * @param utilisateurId l'ID de l'utilisateur.
     * @return la liste des récompenses.
     */
    @GetMapping("/historique/{utilisateurId}")
    public ResponseEntity<List<RecompenseDtoGet>> getHistoriqueRecompenses(@PathVariable Long utilisateurId) {
        List<RecompenseDtoGet> recompenses = recompenseService.getHistoriqueRecompenses(utilisateurId);
        return ResponseEntity.ok(recompenses);
    }
}
