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


    @PostMapping
    public ResponseEntity<RecompenseDtoGet> createRecompense(@RequestBody RecompenseDtoPost dto) {
        RecompenseDtoGet recompenseDto = recompenseService.createRecompense(dto);
        return ResponseEntity.ok(recompenseDto);
    }

    @PostMapping("/generer/{utilisateurId}")
    public ResponseEntity<Void> genererRecompensePourUtilisateur(@PathVariable Long utilisateurId) {
        recompenseService.genererRecompensePourUtilisateur(utilisateurId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/historique/{utilisateurId}")
    public ResponseEntity<List<RecompenseDtoGet>> getHistoriqueRecompenses(@PathVariable Long utilisateurId) {
        List<RecompenseDtoGet> recompenses = recompenseService.getHistoriqueRecompenses(utilisateurId);
        return ResponseEntity.ok(recompenses);
    }
}
