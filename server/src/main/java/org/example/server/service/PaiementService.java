package org.example.server.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.server.dto.paiement.PaiementDtoGet;
import org.example.server.dto.paiement.PaiementDtoPost;
import org.example.server.entity.Commande;
import org.example.server.entity.Paiement;
import org.example.server.entity.Utilisateur;
import org.example.server.enums.StatutPaiement;
import org.example.server.repository.CommandeRepository;
import org.example.server.repository.PaiementRepository;
import org.example.server.repository.UtilisateurRepository;
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
    private CommandeRepository commandeRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private RecompenseService recompenseService;


    @Transactional
    public PaiementDtoGet createPaiement(PaiementDtoPost dtoPost) {
        Paiement paiement = new Paiement();
        paiement.setMontant(dtoPost.getMontant());

        // Définir le statut ou une valeur par défaut
        StatutPaiement statut;
        try {
            statut = StatutPaiement.valueOf(dtoPost.getStatut());
        } catch (IllegalArgumentException e) {
            statut = StatutPaiement.EN_ATTENTE; // Statut par défaut
        }
        paiement.setStatut(statut);

        paiement.setMoyenPaiement(dtoPost.getMoyenPaiement());
        paiement.setDatePaiement(LocalDateTime.now());

        // Assurez-vous que la commande existe avant de l'ajouter
        Commande commande = commandeRepository.findById(dtoPost.getCommandeId())
                .orElseThrow(() -> new RuntimeException("Commande non trouvée pour l'ID : " + dtoPost.getCommandeId()));
        paiement.setCommande(commande);

        Paiement savedPaiement = paiementRepository.save(paiement);

        // Ajouter les points de fidélité si le paiement est validé
        if (statut == StatutPaiement.REUSSI) {
            Utilisateur utilisateur = commande.getUser();
            if (utilisateur != null) {
                // Calculer le montant total de la commande
                double montantTotal = commande.getItemsCommande().stream()
                        .mapToDouble(item -> item.getProduit().getPrix() * item.getQuantite())
                        .sum();

                // Ajouter les points de fidélité (1€ = 1 point)
                int pointsGagnes = (int) montantTotal;
                utilisateur.setPointsFidelite(utilisateur.getPointsFidelite() + pointsGagnes);
                utilisateurRepository.save(utilisateur);

                // Vérifier si les points atteignent ou dépassent 100
                while (utilisateur.getPointsFidelite() >= 100) {
                    recompenseService.genererRecompensePourUtilisateur(utilisateur.getId());
                    utilisateur = utilisateurRepository.findById(utilisateur.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé après mise à jour"));
                }

                System.out.println("Points mis à jour : " + utilisateur.getPointsFidelite());
            }
        }

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
    public boolean validerDetailsBancaires(String numCarte, String titulaireCarte, String dateExpiration, String cvv) {
        // Vérifier si le numéro de carte commence par "4" (exemple fictif)
        if (!numCarte.startsWith("4") || numCarte.length() != 16) {
            return false;
        }
        // Vérifier si le CVV a exactement 3 chiffres
        if (cvv.length() != 3 || !cvv.matches("\\d{3}")) {
            return false;
        }
        // Vérifier le format de la date d'expiration "MM/YY"
        if (!dateExpiration.matches("\\d{2}/\\d{2}")) {
            return false;
        }
        return true;
    }

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


