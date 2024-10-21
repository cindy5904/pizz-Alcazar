package org.example.server.service;

import org.example.server.dto.paiement.PaiementDtoGet;
import org.example.server.dto.paiement.PaiementDtoPost;
import org.example.server.entity.Commande;
import org.example.server.entity.Paiement;
import org.example.server.enums.StatutPaiement;
import org.example.server.repository.CommandeRepository;
import org.example.server.repository.PaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaiementService {
    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private CommandeRepository commandeRepository; // Assurez-vous d'avoir un repository pour Commande

    @Transactional
    public PaiementDtoGet createPaiement(PaiementDtoPost dtoPost) {
        Paiement paiement = new Paiement();
        paiement.setMontant(dtoPost.getMontant());
        paiement.setStatut(StatutPaiement.valueOf(dtoPost.getStatut())); // Assurez-vous que le statut correspond à l'enum
        paiement.setMoyenPaiement(dtoPost.getMoyenPaiement());
        paiement.setDatePaiement(LocalDateTime.parse(dtoPost.getDatePaiement()));

        // Assurez-vous que la commande existe avant de l'ajouter
        Optional<Commande> commandeOptional = commandeRepository.findById(dtoPost.getCommandeId());
        if (commandeOptional.isPresent()) {
            paiement.setCommande(commandeOptional.get());
        } else {
            throw new RuntimeException("Commande non trouvée pour l'ID : " + dtoPost.getCommandeId());
        }

        Paiement savedPaiement = paiementRepository.save(paiement);
        return convertToDtoGet(savedPaiement);
    }

    public PaiementDtoGet getPaiementById(Long id) {
        Optional<Paiement> paiementOptional = paiementRepository.findById(id);
        if (paiementOptional.isPresent()) {
            return convertToDtoGet(paiementOptional.get());
        } else {
            throw new RuntimeException("Paiement non trouvé pour l'ID : " + id);
        }
    }

    public List<PaiementDtoGet> getPaiementsByCommandeId(Long commandeId) {
        List<Paiement> paiements = paiementRepository.findByCommandeId(commandeId);
        return paiements.stream().map(this::convertToDtoGet).toList();
    }

    // Méthode pour convertir Paiement en PaiementDtoGet
    private PaiementDtoGet convertToDtoGet(Paiement paiement) {
        PaiementDtoGet dtoGet = new PaiementDtoGet();
        dtoGet.setId(paiement.getId());
        dtoGet.setMontant(paiement.getMontant());
        dtoGet.setStatut(paiement.getStatut().toString());
        dtoGet.setMoyenPaiement(paiement.getMoyenPaiement());
        dtoGet.setDatePaiement(paiement.getDatePaiement());
        dtoGet.setCommandeId(paiement.getCommande() != null ? paiement.getCommande().getId() : null);
        return dtoGet;
    }
}
