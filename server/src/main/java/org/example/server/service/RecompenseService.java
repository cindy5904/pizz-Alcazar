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


        Recompense recompense = new Recompense();
        recompense.setNom("Remise de 10%");
        recompense.setDescription("Remise automatique pour avoir atteint 100 points de fidélité.");
        recompense.setPointsNecessaires(100);

        String codeRemise = genererCodeRemise();
        recompense.setCodeRemise(codeRemise);
        recompense.setDateRemise(LocalDate.now());

        recompenseRepository.save(recompense);

        utilisateur.setPointsFidelite(utilisateur.getPointsFidelite() - 100);
        utilisateurRepository.save(utilisateur);
    }


    /**
     * Récupérer l'historique des récompenses d'un utilisateur.
     * @param utilisateurId l'ID de l'utilisateur.
     * @return la liste des récompenses sous forme de DTO.
     */
    public List<RecompenseDtoGet> getHistoriqueRecompenses(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // Récupérer toutes les récompenses de cet utilisateur
        List<Recompense> recompenses = recompenseRepository.findByUser(utilisateur);

        // Convertir en DTO
        return recompenses.stream()
                .map(this::convertToDtoGet)
                .collect(Collectors.toList());
    }

    /**
     * Convertir une entité Recompense en DTO RecompenseDtoGet.
     * @param recompense l'entité Recompense.
     * @return le DTO correspondant.
     */
    private RecompenseDtoGet convertToDtoGet(Recompense recompense) {
        RecompenseDtoGet dto = new RecompenseDtoGet();
        dto.setId(recompense.getId());
        dto.setNom(recompense.getNom());
        dto.setDescription(recompense.getDescription());
        dto.setPointsNecessaires(recompense.getPointsNecessaires());
        dto.setDateRemise(recompense.getDateRemise()); // Champ à ajouter dans l'entité Recompense
        dto.setCodeRemise(recompense.getCodeRemise()); // Champ à ajouter dans l'entité Recompense
        return dto;
    }

    /**
     * Générer un code unique pour une récompense.
     * @return le code de remise.
     */
    private String genererCodeRemise() {
        return UUID.randomUUID().toString().substring(0, 8); // Génère un code unique à partir d'un UUID
    }

}
