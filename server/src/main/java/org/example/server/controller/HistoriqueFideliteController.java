package org.example.server.controller;

import org.example.server.dto.historiqueFidelite.HistoriqueFideliteDtoGet;
import org.example.server.dto.historiqueFidelite.HistoriqueFideliteDtoPost;
import org.example.server.entity.HistoriqueFidelite;
import org.example.server.service.HistoriqueFideliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/historique-fidelite")
@PreAuthorize("hasRole('ADMIN')")
public class HistoriqueFideliteController {
    @Autowired
    private HistoriqueFideliteService historiqueFideliteService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<HistoriqueFideliteDtoGet> createHistoriqueFidelite(@RequestBody HistoriqueFideliteDtoPost dtoPost) {
        HistoriqueFideliteDtoGet createdHistorique = historiqueFideliteService.createHistoriqueFidelite(dtoPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHistorique);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<HistoriqueFideliteDtoGet>> getHistoriqueByUserId(@PathVariable Long userId) {
        List<HistoriqueFidelite> historiques = historiqueFideliteService.findByUserId(userId);
        List<HistoriqueFideliteDtoGet> historiqueDtos = historiques.stream()
                .map(historiqueFideliteService::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(historiqueDtos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/mois/{annee}/{mois}")
    public ResponseEntity<List<HistoriqueFideliteDtoGet>> getHistoriqueParMois(@PathVariable int annee, @PathVariable int mois) {
        List<HistoriqueFideliteDtoGet> historiques = historiqueFideliteService.getHistoriqueParMois(annee, mois);
        return ResponseEntity.ok(historiques);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/compte-recompenses/{annee}/{mois}")
    public ResponseEntity<Long> countRecompensesParMois(@PathVariable int annee, @PathVariable int mois) {
        long count = historiqueFideliteService.countRecompensesParMois(annee, mois);
        return ResponseEntity.ok(count);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/commandes-par-semaine")
    public ResponseEntity<List<Long>> getCommandesParSemaine(@RequestParam String dateDebut) {
        LocalDate date = LocalDate.parse(dateDebut, DateTimeFormatter.ISO_DATE);
        List<Long> commandesParSemaine = historiqueFideliteService.getCommandesParSemaine(date);
        return ResponseEntity.ok(commandesParSemaine);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/recompenses-par-semaine")
    public ResponseEntity<Long> getRecompensesParSemaine(@RequestParam String dateDebut) {
        LocalDate date = LocalDate.parse(dateDebut, DateTimeFormatter.ISO_DATE);
        long recompensesParSemaine = historiqueFideliteService.countRecompensesParSemaine(date);
        return ResponseEntity.ok(recompensesParSemaine);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/comparaison-commandes")
    public ResponseEntity<Double> getComparaisonCommandes(@RequestParam String semaineActuelle) {
        LocalDate date = LocalDate.parse(semaineActuelle, DateTimeFormatter.ISO_DATE);
        double comparaison = historiqueFideliteService.compareSemaineCommandes(date);
        return ResponseEntity.ok(comparaison);
    }
}
