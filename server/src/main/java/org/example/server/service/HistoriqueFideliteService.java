package org.example.server.service;

import org.example.server.dto.historiqueFidelite.HistoriqueFideliteDtoGet;
import org.example.server.dto.historiqueFidelite.HistoriqueFideliteDtoPost;
import org.example.server.entity.HistoriqueFidelite;
import org.example.server.entity.Recompense;
import org.example.server.entity.Utilisateur;
import org.example.server.exception.ResourceNotFoundException;
import org.example.server.repository.HistoriqueFideliteRepository;
import org.example.server.repository.RecompenseRepository;
import org.example.server.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoriqueFideliteService {
    @Autowired
    private HistoriqueFideliteRepository historiqueFideliteRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RecompenseRepository recompenseRepository;

    public HistoriqueFideliteDtoGet createHistoriqueFidelite(HistoriqueFideliteDtoPost dtoPost) {
        Utilisateur utilisateur = utilisateurRepository.findById(dtoPost.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Recompense recompense = recompenseRepository.findById(dtoPost.getRecompenseId())
                .orElseThrow(() -> new RuntimeException("Récompense non trouvée"));

        HistoriqueFidelite historique = new HistoriqueFidelite();
        historique.setUser(utilisateur);
        historique.setRecompense(recompense);
        historique.setDateTransaction(LocalDateTime.parse(dtoPost.getDateTransaction()));

        historique = historiqueFideliteRepository.save(historique);

        return convertToDto(historique);
    }

    public List<HistoriqueFidelite> findByUserId(Long userId) {
        return historiqueFideliteRepository.findByUserId(userId);
    }

    public List<HistoriqueFideliteDtoGet> getHistoriqueParMois(int annee, int mois) {
        LocalDate debut = LocalDate.of(annee, mois, 1);
        LocalDate fin = debut.plusMonths(1);

        List<HistoriqueFidelite> historiques = historiqueFideliteRepository.findByDateTransactionBetween(debut.atStartOfDay(), fin.atStartOfDay());
        return historiques.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public long countRecompensesParMois(int annee, int mois) {
        // Récupération de l'historique des récompenses pour le mois spécifié
        List<HistoriqueFideliteDtoGet> historiques = getHistoriqueParMois(annee, mois);

        // Compter le nombre total de récompenses
        return historiques.stream()
                .mapToInt(h -> h.getRecompenseId() != null ? 1 : 0) // S'assurer qu'on ne compte que les entrées valides
                .sum();
    }


    // Convertisseur pour DTO
    public HistoriqueFideliteDtoGet convertToDto(HistoriqueFidelite historique) {
        HistoriqueFideliteDtoGet dto = new HistoriqueFideliteDtoGet();
        dto.setId(historique.getId());
        dto.setUserId(historique.getUser().getId());
        dto.setRecompenseId(historique.getRecompense().getId());
        dto.setDateTransaction(historique.getDateTransaction());
        return dto;
    }
}
