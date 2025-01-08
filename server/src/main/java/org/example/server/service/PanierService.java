package org.example.server.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.server.dto.panier.PanierDtoGet;
import org.example.server.dto.panier.PanierDtoPost;
import org.example.server.dto.panierItem.PanierItemDtoGet;
import org.example.server.dto.panierItem.PanierItemDtoPost;
import org.example.server.dto.produit.ProduitDtoGet;
import org.example.server.dto.user.UtilisateurDtoGet;
import org.example.server.entity.Panier;
import org.example.server.entity.PanierItem;
import org.example.server.entity.Produit;
import org.example.server.entity.Utilisateur;
import org.example.server.repository.PanierItemRepository;
import org.example.server.repository.PanierRepository;
import org.example.server.repository.ProduitRepository;
import org.example.server.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PanierService {
    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private PanierItemRepository panierItemRepository;


    public PanierDtoGet addOrUpdatePanierItem(Long userId, PanierItemDtoPost itemDto) {
        System.out.println("Tentative d'ajout ou de mise à jour d'un produit dans le panier pour l'utilisateur ID : " + userId);

        // 1. Recherchez le panier actif de l'utilisateur ou créez-en un nouveau s'il n'existe pas
        Panier panier = panierRepository.findByUserIdAndActifTrue(userId)
                .orElseGet(() -> {
                    Panier newPanier = new Panier();
                    Utilisateur user = utilisateurRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
                    newPanier.setUser(user);
                    newPanier.setDateCreation(LocalDateTime.now());
                    newPanier.setActif(true);
                    Panier savedPanier = panierRepository.save(newPanier);

                    // Désactiver les anciens paniers actifs pour cet utilisateur
                    panierRepository.deactivateOtherPaniers(userId, savedPanier.getId());

                    return savedPanier;
                });

        // 2. Vérifiez si le produit est déjà dans le panier
        PanierItem existingItem = panier.getItemsPanier().stream()
                .filter(item -> item.getProduit().getId().equals(itemDto.getProduitId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // 3. Si le produit existe déjà, augmentez sa quantité
            existingItem.setQuantite(existingItem.getQuantite() + itemDto.getQuantite());
            panierItemRepository.save(existingItem);
        } else {
            // 4. Si le produit n'est pas dans le panier, créez un nouvel élément
            Produit produit = produitRepository.findById(itemDto.getProduitId())
                    .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé"));

            PanierItem newItem = new PanierItem();
            newItem.setProduit(produit);
            newItem.setQuantite(itemDto.getQuantite());
            newItem.setPanier(panier);
            panier.getItemsPanier().add(newItem);
            panierItemRepository.save(newItem);
        }

        // 5. Mettez à jour la date de modification et sauvegardez le panier
        panier.setDateModification(LocalDateTime.now());
        panierRepository.save(panier);

        System.out.println("Panier mis à jour avec succès : " + panier);
        return mapToDtoGet(panier); // Retourner le panier mis à jour
    }




    public PanierDtoGet getPanierByUserId(Long userId) {
        Panier panier = panierRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Panier non trouvé pour l'utilisateur"));
        return mapToDtoGet(panier);
    }

    public PanierDtoGet getPanierActifByUserId(Long userId) {
        Panier panier = panierRepository.findByUserIdAndActifTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun panier actif pour l'utilisateur"));
        return mapToDtoGet(panier);
    }

    public void archiverPanier(Long panierId) {
        Panier panier = panierRepository.findById(panierId)
                .orElseThrow(() -> new EntityNotFoundException("Panier non trouvé"));
        panier.setActif(false); // Marque le panier comme inactif
        panierRepository.save(panier);
    }
    private PanierItemDtoGet mapPanierItemToDto(PanierItem item) {
        PanierItemDtoGet itemDto = new PanierItemDtoGet();
        itemDto.setId(item.getId());
        itemDto.setQuantite(item.getQuantite());

        // Mappage du produit associé
        Produit produit = item.getProduit();
        ProduitDtoGet produitDto = new ProduitDtoGet();
        produitDto.setId(produit.getId());
        produitDto.setNom(produit.getNom());
        produitDto.setDescription(produit.getDescription());
        produitDto.setPrix(produit.getPrix());
        itemDto.setProduit(produitDto);

        return itemDto;
    }



    private PanierDtoGet mapToDtoGet(Panier panier) {
        PanierDtoGet dtoGet = new PanierDtoGet();
        dtoGet.setId(panier.getId());
        dtoGet.setDateCreation(LocalDate.from(panier.getDateCreation()));
        dtoGet.setDateModification(LocalDate.from(panier.getDateModification()));

        // Mappage de l'utilisateur
        Utilisateur user = panier.getUser();
        UtilisateurDtoGet userDto = new UtilisateurDtoGet();
        userDto.setId(user.getId());
        userDto.setNom(user.getNom());
        userDto.setPrenom(user.getPrenom());
        dtoGet.setUser(userDto);

        // Mappage des itemsPanier
        List<PanierItemDtoGet> itemsDto = panier.getItemsPanier().stream()
                .map(this::mapPanierItemToDto)
                .collect(Collectors.toList());
        dtoGet.setItemsPanier(itemsDto);

        return dtoGet;
    }


    public List<Produit> getProduitsByPanierId(Long panierId) {
        List<PanierItem> panierItems = panierItemRepository.findByPanierId(panierId);
        return panierItems.stream()
                .map(PanierItem::getProduit)
                .collect(Collectors.toList());
    }

}


