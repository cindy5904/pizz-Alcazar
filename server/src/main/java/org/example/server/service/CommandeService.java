package org.example.server.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.server.dto.commande.CommandeDtoGet;
import org.example.server.dto.commande.CommandeDtoPost;
import org.example.server.dto.commandeItem.CommandeItemDtoGet;
import org.example.server.entity.Commande;
import org.example.server.entity.Panier;
import org.example.server.entity.Utilisateur;
import org.example.server.enums.EtatCommande;
import org.example.server.exception.ResourceNotFoundException;
import org.example.server.repository.CommandeRepository;
import org.example.server.repository.PaiementRepository;
import org.example.server.repository.PanierRepository;
import org.example.server.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CommandeService {
    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PanierRepository panierRepository; // Ajout du PanierRepository

    @Autowired
    private PaiementRepository paiementRepository; // Ajout du PaiementRepository

    public CommandeDtoGet convertToDto(Commande commande) {
        CommandeDtoGet dto = new CommandeDtoGet();
        dto.setId(commande.getId());
        dto.setDetailsCommande(commande.getDetailsCommande()); // Assurez-vous que ceci est bien défini
        dto.setStatut(commande.getStatut());
        dto.setAdresseLivraison(commande.getAdresseLivraison());
        dto.setTelephone(commande.getTelephone());
        dto.setTypeLivraison(commande.getTypeLivraison());

        if (commande.getUser() != null) {
            dto.setUserId(commande.getUser().getId()); // Assurez-vous que l'utilisateur est bien lié
        }

        if (commande.getPanier() != null) {
            dto.setPanierId(commande.getPanier().getId()); // Assurez-vous que le panier est bien lié
        }

        return dto;
    }



    public CommandeDtoGet createCommande(CommandeDtoPost commandeDto) {
        Commande commande = new Commande();
        commande.setDetailsCommande(commandeDto.getDetailsCommande()); // Assurez-vous que ceci est bien défini
        commande.setStatut(commandeDto.getStatut()); // Assurez-vous que le statut est défini

        // Récupérer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(commandeDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + commandeDto.getUserId()));
        commande.setUser(utilisateur); // Lier l'utilisateur à la commande

        // Récupérer le panier si nécessaire
        Panier panier = panierRepository.findById(commandeDto.getPanierId())
                .orElseThrow(() -> new ResourceNotFoundException("Panier non trouvé avec l'ID: " + commandeDto.getPanierId()));
        commande.setPanier(panier); // Lier le panier à la commande

        // Ajoutez d'autres champs si nécessaire
        commande.setAdresseLivraison(commandeDto.getAdresseLivraison());
        commande.setTelephone(commandeDto.getTelephone());

        Commande savedCommande = commandeRepository.save(commande);
        return convertToDto(savedCommande);
    }


    public CommandeDtoGet getCommandeById(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID: " + id));
        return convertToDto(commande);
    }

    public List<CommandeDtoGet> getAllCommandes() {
        return StreamSupport.stream(commandeRepository.findAll().spliterator(), false)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteCommande(Long id) {
        if (!commandeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Commande non trouvée avec l'ID: " + id);
        }
        commandeRepository.deleteById(id);
    }

    public CommandeDtoGet updateCommande(Long id, CommandeDtoPost commandeDto) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID: " + id));

        commande.setDetailsCommande(commandeDto.getDetailsCommande());
        commande.setStatut(EtatCommande.valueOf(String.valueOf(commandeDto.getStatut())));

        Commande updatedCommande = commandeRepository.save(commande);
        return convertToDto(updatedCommande);
    }

    public CommandeDtoGet getCommandeWithPaiementsById(Long id) {
        Commande commande = commandeRepository.findCommandeWithPaiementById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID: " + id));
        return convertToDto(commande);
    }

    public void validerCommande(Long commandeId) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new EntityNotFoundException("Commande non trouvée"));

        Utilisateur utilisateur = commande.getUser();
        System.out.println("Utilisateur avant calcul: " + utilisateur);
        double totalCommande = calculerTotalCommande(commande);
        int pointsGagnes = (int) totalCommande;
        System.out.println("Total commande: " + totalCommande);
        System.out.println("Points gagnés: " + pointsGagnes);

        // Mise à jour des points de fidélité
        utilisateur.setPointsFidelite(utilisateur.getPointsFidelite() + pointsGagnes);
        System.out.println("Utilisateur après ajout de points: " + utilisateur);

        // Appliquer automatiquement la remise si 100 points ou plus sont atteints
        while (utilisateur.getPointsFidelite() >= 100) {
            appliquerRemise(utilisateur);
        }

        utilisateurRepository.save(utilisateur);
        System.out.println("Utilisateur après sauvegarde: " + utilisateur);
    }

    private double calculerTotalCommande(Commande commande) {
        return commande.getItemsCommande().stream()
                .mapToDouble(item -> item.getProduit().getPrix() * item.getQuantite())
                .sum();
    }

    private void appliquerRemise(Utilisateur utilisateur) {
        // Appliquer une remise de 10% et réinitialiser les points de fidélité
        System.out.println("Une remise de 10% a été appliquée à l'utilisateur " + utilisateur.getId());

        // Déduire 100 points après application de la remise
        utilisateur.setPointsFidelite(utilisateur.getPointsFidelite() - 100);
    }
}
