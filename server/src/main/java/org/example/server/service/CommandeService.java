package org.example.server.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.server.dto.commande.CommandeDtoGet;
import org.example.server.dto.commande.CommandeDtoPost;
import org.example.server.dto.commandeItem.CommandeItemDtoGet;
import org.example.server.entity.Commande;
import org.example.server.entity.CommandeItem;
import org.example.server.entity.Panier;
import org.example.server.entity.Utilisateur;
import org.example.server.enums.EtatCommande;
import org.example.server.exception.ResourceNotFoundException;
import org.example.server.repository.CommandeRepository;
import org.example.server.repository.PaiementRepository;
import org.example.server.repository.PanierRepository;
import org.example.server.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private PanierRepository panierRepository;

    @Autowired
    private PaiementRepository paiementRepository;

    public CommandeDtoGet convertToDto(Commande commande) {
        CommandeDtoGet dto = new CommandeDtoGet();
        dto.setId(commande.getId());
        dto.setNumeroCommande(commande.getNumeroCommande());
        dto.setDetailsCommande(commande.getDetailsCommande());
        dto.setStatut(commande.getStatut());
        dto.setAdresseLivraison(commande.getAdresseLivraison());
        dto.setTelephone(commande.getTelephone());
        dto.setTypeLivraison(commande.getTypeLivraison());

        if (commande.getUser() != null) {
            dto.setUserId(commande.getUser().getId());
        }

        if (commande.getPanier() != null) {
            dto.setPanierId(commande.getPanier().getId());
        }
        if (commande.getPaiement() != null) {
            dto.setPaiementId(commande.getPaiement().getId());
        } else {

            paiementRepository.findById(commande.getId()).ifPresent(paiement -> {
                dto.setPaiementId(paiement.getId());
            });
        }

        List<CommandeItemDtoGet> itemsDto = commande.getItemsCommande().stream()
                .map(this::convertCommandeItemToDto)
                .collect(Collectors.toList());
        dto.setItemsCommande(itemsDto);
        System.out.println("Items convertis en DTO : " + itemsDto);

        return dto;
    }

    private CommandeItemDtoGet convertCommandeItemToDto(CommandeItem item) {
        CommandeItemDtoGet dto = new CommandeItemDtoGet();
        dto.setId(item.getId());
        dto.setQuantite(item.getQuantite());
        dto.setProduitId(item.getProduit().getId());
        dto.setProduitNom(item.getProduit().getNom());
        dto.setProduitPrix(item.getProduit().getPrix());
        dto.setCommandeId(item.getCommande().getId());
        System.out.println("Commande ID pour l'item : " + item.getCommande().getId());

        return dto;
    }

    public CommandeDtoGet createCommande(CommandeDtoPost commandeDto) {
        Commande commande = new Commande();
        commande.setDetailsCommande(commandeDto.getDetailsCommande());
        commande.setStatut(commandeDto.getStatut());

        Utilisateur utilisateur = utilisateurRepository.findById(commandeDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + commandeDto.getUserId()));
        commande.setUser(utilisateur);

        Panier panier = panierRepository.findById(commandeDto.getPanierId())
                .orElseThrow(() -> new ResourceNotFoundException("Panier non trouvé avec l'ID: " + commandeDto.getPanierId()));
        commande.setPanier(panier);

        List<CommandeItem> itemsCommande = panier.getItemsPanier().stream().map(panierItem -> {
            CommandeItem commandeItem = new CommandeItem();
            commandeItem.setProduit(panierItem.getProduit());
            commandeItem.setQuantite(panierItem.getQuantite());
            commandeItem.setCommande(commande);
            return commandeItem;
        }).collect(Collectors.toList());
        commande.setItemsCommande(itemsCommande);
        Commande savedCommande = commandeRepository.save(commande);
        panier.setActif(false);
        panierRepository.save(panier);
        return convertToDto(savedCommande);
    }



    public CommandeDtoGet getCommandeById(Long id) {
        Commande commande = commandeRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID: " + id));
        return convertToDto(commande);
    }


    public Page<CommandeDtoGet> getAllCommandes(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID de l'utilisateur est requis pour récupérer les commandes.");
        }

        // Utilisez directement le `Page` retourné par le repository
        Page<Commande> commandesPage = commandeRepository.findByUserId(userId, pageable);

        // Transformez les éléments de `Page<Commande>` en `Page<CommandeDtoGet>`
        return commandesPage.map(this::convertToDto);
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
