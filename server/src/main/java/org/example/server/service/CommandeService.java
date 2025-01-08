package org.example.server.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.server.dto.commande.CommandeDtoGet;
import org.example.server.dto.commande.CommandeDtoPost;
import org.example.server.dto.commandeItem.CommandeItemDtoGet;
import org.example.server.dto.panier.PanierDtoGet;
import org.example.server.dto.user.UtilisateurDtoGet;
import org.example.server.entity.*;
import org.example.server.enums.EtatCommande;
import org.example.server.enums.StatutPaiement;
import org.example.server.exception.ResourceNotFoundException;
import org.example.server.repository.CommandeRepository;
import org.example.server.repository.PaiementRepository;
import org.example.server.repository.PanierRepository;
import org.example.server.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
            UtilisateurDtoGet userDto = new UtilisateurDtoGet();
            userDto.setId(commande.getUser().getId());
            userDto.setNom(commande.getUser().getNom());
            userDto.setPrenom(commande.getUser().getPrenom());
            userDto.setEmail(commande.getUser().getEmail());
            userDto.setAdresse(commande.getUser().getAdresse());
            userDto.setTelephone(commande.getUser().getTelephone());
            userDto.setPointsFidelite(commande.getUser().getPointsFidelite());
            userDto.setRoles(commande.getUser().getUserRoles().stream().map(Role::getName).collect(Collectors.toSet())); // Ajouter les rôles
            dto.setUser(userDto);
        }


        if (commande.getPanier() != null) {
            PanierDtoGet panierDto = new PanierDtoGet();
            panierDto.setId(commande.getPanier().getId());
            dto.setPanier(panierDto);
        }

        if (commande.getPaiement() != null) {
            dto.setPaiementId(commande.getPaiement().getId());

        }


        if (commande.getItemsCommande() != null && !commande.getItemsCommande().isEmpty()) {
            List<CommandeItemDtoGet> itemsDto = commande.getItemsCommande().stream()
                    .map(this::convertCommandeItemToDto)
                    .collect(Collectors.toList());
            dto.setItemsCommande(itemsDto);
        }

        return dto;
    }


    private CommandeItemDtoGet convertCommandeItemToDto(CommandeItem item) {
        CommandeItemDtoGet dto = new CommandeItemDtoGet();
        dto.setId(item.getId());
        dto.setQuantite(item.getQuantite());
        dto.setProduitId(item.getProduit().getId());
        dto.setProduitNom(item.getProduit().getNom());
        dto.setProduitPrix(item.getProduit().getPrix());
        dto.setCommandeId(item.getCommande() != null ? item.getCommande().getId() : null); // Vérification de null pour éviter les erreurs
        System.out.println("Commande ID pour l'item : " + (item.getCommande() != null ? item.getCommande().getId() : "null"));

        return dto;
    }

    @Transactional
    public CommandeDtoGet createCommande(CommandeDtoPost commandeDto) {
        Commande commande = new Commande();
        commande.setDetailsCommande(commandeDto.getDetailsCommande());
        commande.setStatut(commandeDto.getStatut());

        Utilisateur utilisateur = utilisateurRepository.findById(commandeDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + commandeDto.getUserId()));
        commande.setUser(utilisateur);
        commande.setAdresseLivraison(
                commandeDto.getAdresseLivraison() != null && !commandeDto.getAdresseLivraison().isEmpty()
                        ? commandeDto.getAdresseLivraison()
                        : utilisateur.getAdresse()
        );

        commande.setTelephone(
                commandeDto.getTelephone() != null && !commandeDto.getTelephone().isEmpty()
                        ? commandeDto.getTelephone()
                        : utilisateur.getTelephone()
        );


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
        System.out.println("Commande sauvegardée : " + savedCommande);
        System.out.println("Adresse Livraison : " + commande.getAdresseLivraison());
        System.out.println("Téléphone : " + commande.getTelephone());
        System.out.println("Utilisateur lié : " + commande.getUser());


        panier.setActif(false);
        panierRepository.save(panier);

        if (commandeDto.getPaiementId() != null) {
            Paiement paiement = paiementRepository.findById(commandeDto.getPaiementId())
                    .orElseThrow(() -> new ResourceNotFoundException("Paiement non trouvé avec l'ID: " + commandeDto.getPaiementId()));

            setPaiementForCommande(savedCommande, paiement);
            paiementRepository.save(paiement);
        }

        return convertToDto(savedCommande);
    }
    private void setPaiementForCommande(Commande commande, Paiement paiement) {
        commande.setPaiement(paiement);
        paiement.setCommande(commande);
    }
    public List<CommandeDtoGet> getCommandesPayeesEtEnCours() {
        // Appelle la méthode du repository avec les deux filtres
        List<Commande> commandes = commandeRepository.findCommandesByCommandeStatutAndPaiementStatut(
                EtatCommande.EN_COURS, // Commande en cours
                StatutPaiement.REUSSI  // Paiement réussi
        );

        // Convertit les entités Commande en DTO
        return commandes.stream()
                .map(this::convertToDto) // Utilise ta méthode existante pour convertir
                .collect(Collectors.toList());
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

        Page<Commande> commandesPage = commandeRepository.findByUserId(userId, pageable);
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


        utilisateur.setPointsFidelite(utilisateur.getPointsFidelite() + pointsGagnes);
        System.out.println("Utilisateur après ajout de points: " + utilisateur);


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

        System.out.println("Une remise de 10% a été appliquée à l'utilisateur " + utilisateur.getId());

        // Déduire 100 points après application de la remise
        utilisateur.setPointsFidelite(utilisateur.getPointsFidelite() - 100);
    }
    public List<CommandeDtoGet> getCommandesParSemaine(LocalDate dateDebut) {
        LocalDateTime startDate = dateDebut.atStartOfDay();
        LocalDateTime endDate = dateDebut.plusDays(7).atStartOfDay();

        System.out.println("=== Début de getCommandesParSemaine ===");
        System.out.println("Start Date : " + startDate);
        System.out.println("End Date : " + endDate);

        // Récupération des commandes via le repository
        List<Commande> commandes = paiementRepository.findCommandesByPaiementDate(startDate);

        System.out.println("=== Résultat ===");
        System.out.println("Nombre de commandes trouvées : " + commandes.size());
        commandes.forEach(c -> {
            System.out.println("Commande ID : " + c.getId());
            System.out.println("Paiement ID : " + (c.getPaiement() != null ? c.getPaiement().getId() : "Pas de paiement"));
        });


        return commandes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    public List<CommandeDtoGet> getCommandesPayees() {
        List<Commande> commandes = paiementRepository.findCommandesByPaiementStatut(StatutPaiement.REUSSI);

        return commandes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    public CommandeDtoGet updateStatutCommande(Long id, String nouveauStatut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID : " + id));

        commande.setStatut(EtatCommande.valueOf(nouveauStatut));
        Commande updatedCommande = commandeRepository.save(commande);

        return convertToDto(updatedCommande);
    }

    public CommandeDtoGet updateCommandeStatut(Long id, String nouveauStatut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID: " + id));

        System.out.println("Statut actuel : " + commande.getStatut());
        System.out.println("Nouveau statut reçu : " + nouveauStatut);

        try {
            EtatCommande statut = EtatCommande.valueOf(nouveauStatut);
            commande.setStatut(statut);
        } catch (IllegalArgumentException e) {
            System.err.println("Statut invalide reçu : " + nouveauStatut);
            throw new IllegalArgumentException("Statut invalide: " + nouveauStatut);
        }

        Commande updatedCommande = commandeRepository.save(commande);

        System.out.println("Commande mise à jour : " + updatedCommande);

        return convertToDto(updatedCommande);
    }




}
