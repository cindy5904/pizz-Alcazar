package org.example.server.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.server.dto.recompense.RecompenseDtoGet;
import org.example.server.dto.recompense.RecompenseDtoPost;
import org.example.server.entity.Recompense;
import org.example.server.entity.Utilisateur;
import org.example.server.repository.RecompenseRepository;
import org.example.server.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecompenseService {
    @Autowired
    private RecompenseRepository recompenseRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;


    public RecompenseDtoGet createRecompense(RecompenseDtoPost dto) {
        Recompense recompense = new Recompense();
        recompense.setNom(dto.getNom());
        recompense.setDescription(dto.getDescription());
        recompense.setPointsNecessaires(dto.getPointsNecessaires());

        Recompense savedRecompense = recompenseRepository.save(recompense);

        return convertToDtoGet(savedRecompense);
    }


    public void genererRecompensePourUtilisateur(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        while (utilisateur.getPointsFidelite() >= 100) {
            Recompense recompense = new Recompense();
            recompense.setNom("Remise de 10%");
            recompense.setDescription("Remise automatique pour avoir atteint 100 points de fidélité.");
            recompense.setPointsNecessaires(100);
            recompense.setCodeRemise(genererCodeRemise());
            recompense.setDateRemise(LocalDate.now());
            recompense.setUser(utilisateur); // Associer l'utilisateur à la récompense

            recompenseRepository.save(recompense);

            utilisateur.setPointsFidelite(utilisateur.getPointsFidelite() - 100);
            System.out.println("Utilisateur associé : " + utilisateur.getId());
            System.out.println("Récompense utilisateur avant sauvegarde : " + recompense.getUser().getId());
            recompenseRepository.save(recompense);

        }
    }



    public List<RecompenseDtoGet> getHistoriqueRecompenses(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // Récupérer toutes les récompenses de cet utilisateur
        List<Recompense> recompenses = recompenseRepository.findByUser(utilisateur);
        System.out.println("Récompenses trouvées pour l'utilisateur : " + recompenses);

        // Convertir en DTO
        return recompenses.stream()
                .map(this::convertToDtoGet)
                .collect(Collectors.toList());
    }

    private RecompenseDtoGet convertToDtoGet(Recompense recompense) {
        RecompenseDtoGet dto = new RecompenseDtoGet();
        dto.setId(recompense.getId());
        dto.setNom(recompense.getNom());
        dto.setDescription(recompense.getDescription());
        dto.setPointsNecessaires(recompense.getPointsNecessaires());
        dto.setDateRemise(recompense.getDateRemise());
        dto.setCodeRemise(recompense.getCodeRemise());
        return dto;
    }


    private String genererCodeRemise() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

}
