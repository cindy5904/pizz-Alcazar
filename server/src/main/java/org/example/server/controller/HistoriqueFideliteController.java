package org.example.server.controller;

import org.example.server.dto.historiqueFidelite.HistoriqueFideliteDtoGet;
import org.example.server.dto.historiqueFidelite.HistoriqueFideliteDtoPost;
import org.example.server.entity.HistoriqueFidelite;
import org.example.server.service.HistoriqueFideliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/historique-fidelite")
public class HistoriqueFideliteController {
    @Autowired
    private HistoriqueFideliteService historiqueFideliteService;

    // Endpoint pour créer un nouvel historique de fidélité
    @PostMapping
    public ResponseEntity<HistoriqueFideliteDtoGet> createHistoriqueFidelite(@RequestBody HistoriqueFideliteDtoPost dtoPost) {
        HistoriqueFideliteDtoGet createdHistorique = historiqueFideliteService.createHistoriqueFidelite(dtoPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHistorique);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<HistoriqueFideliteDtoGet>> getHistoriqueByUserId(@PathVariable Long userId) {
        List<HistoriqueFidelite> historiques = historiqueFideliteService.findByUserId(userId);
        List<HistoriqueFideliteDtoGet> historiqueDtos = historiques.stream()
                .map(historiqueFideliteService::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(historiqueDtos);
    }


    @GetMapping("/mois/{annee}/{mois}")
    public ResponseEntity<List<HistoriqueFideliteDtoGet>> getHistoriqueParMois(@PathVariable int annee, @PathVariable int mois) {
        List<HistoriqueFideliteDtoGet> historiques = historiqueFideliteService.getHistoriqueParMois(annee, mois);
        return ResponseEntity.ok(historiques);
    }

    @GetMapping("/compte-recompenses/{annee}/{mois}")
    public ResponseEntity<Long> countRecompensesParMois(@PathVariable int annee, @PathVariable int mois) {
        long count = historiqueFideliteService.countRecompensesParMois(annee, mois);
        return ResponseEntity.ok(count);
    }
}
