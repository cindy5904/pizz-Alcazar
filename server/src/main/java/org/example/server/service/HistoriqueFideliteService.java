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
import java.util.Map;
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
        List<HistoriqueFideliteDtoGet> historiques = getHistoriqueParMois(annee, mois);


        return historiques.stream()
                .mapToInt(h -> h.getRecompenseId() != null ? 1 : 0)
                .sum();
    }
    public List<Long> getCommandesParSemaine(LocalDate dateDebut) {
        LocalDate dateFin = dateDebut.plusDays(7);
        System.out.println("=== LOG: Commandes par semaine ===");
        System.out.println("Date début : " + dateDebut);
        System.out.println("Date fin : " + dateFin);

        List<Long> commandes = historiqueFideliteRepository.findByDateTransactionBetween(dateDebut.atStartOfDay(), dateFin.atStartOfDay())
                .stream()
                .collect(Collectors.groupingBy(
                        historique -> historique.getDateTransaction().toLocalDate(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        System.out.println("Commandes trouvées : " + commandes);
        return commandes;
    }

    public long countRecompensesParSemaine(LocalDate dateDebut) {
        LocalDate dateFin = dateDebut.plusDays(7);
        return historiqueFideliteRepository.findByDateTransactionBetween(dateDebut.atStartOfDay(), dateFin.atStartOfDay())
                .stream()
                .filter(h -> h.getRecompense() != null)
                .count();
    }
    public double compareSemaineCommandes(LocalDate semaineActuelle) {
        LocalDate semainePrecedente = semaineActuelle.minusWeeks(1);

        System.out.println("=== LOG: Comparaison des commandes ===");
        System.out.println("Semaine actuelle : " + semaineActuelle);
        System.out.println("Semaine précédente : " + semainePrecedente);

        long totalActuel = getCommandesParSemaine(semaineActuelle).stream().mapToLong(Long::longValue).sum();
        long totalPrecedent = getCommandesParSemaine(semainePrecedente).stream().mapToLong(Long::longValue).sum();

        System.out.println("Total actuel : " + totalActuel);
        System.out.println("Total précédent : " + totalPrecedent);

        if (totalPrecedent == 0) {
            return 0.0;
        }

        double pourcentage = (double) (totalActuel - totalPrecedent) / totalPrecedent * 100;
        System.out.println("Pourcentage de variation : " + pourcentage);
        return pourcentage;
    }


    public HistoriqueFideliteDtoGet convertToDto(HistoriqueFidelite historique) {
        HistoriqueFideliteDtoGet dto = new HistoriqueFideliteDtoGet();
        dto.setId(historique.getId());
        dto.setUserId(historique.getUser().getId());
        dto.setRecompenseId(historique.getRecompense().getId());
        dto.setDateTransaction(historique.getDateTransaction());
        return dto;
    }
}
