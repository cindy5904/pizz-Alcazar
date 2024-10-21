package org.example.server.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.server.dto.panier.PanierDtoGet;
import org.example.server.dto.panier.PanierDtoPost;
import org.example.server.dto.panierItem.PanierItemDtoPost;
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


    public PanierDtoGet createPanier(PanierDtoPost dtoPost) {
        System.out.println("Démarrage de la création du panier pour l'utilisateur ID : " + dtoPost.getUserId());

        // Récupérer l'utilisateur
        Utilisateur user = utilisateurRepository.findById(dtoPost.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        System.out.println("Utilisateur trouvé : " + user);

        // Validation de la liste des items
        if (dtoPost.getItemsPanier() == null || dtoPost.getItemsPanier().isEmpty()) {
            throw new IllegalArgumentException("La liste des items ne peut pas être vide");
        }

        LocalDateTime now = LocalDateTime.now();
        Panier panier = new Panier();
        panier.setDateCreation(now);
        panier.setDateModification(now);
        panier.setUser(user);
        System.out.println("Panier créé : " + panier);

        List<PanierItem> itemsPanier = new ArrayList<>();
        for (PanierItemDtoPost itemDto : dtoPost.getItemsPanier()) {
            Produit produit = produitRepository.findById(itemDto.getProduitId()).orElse(null);
            if (produit == null) {
                System.out.println("Produit avec ID " + itemDto.getProduitId() + " non trouvé. Ignoré.");
                continue; // Ignore cet article
            }

            PanierItem item = new PanierItem();
            item.setProduit(produit);
            item.setQuantite(itemDto.getQuantite());
            item.setPanier(panier);
            itemsPanier.add(item);
            System.out.println("Article ajouté au panier : " + item);
        }

        panier.setItemsPanier(itemsPanier);
        panier = panierRepository.save(panier); // Assurez-vous que l'ID est généré ici
        System.out.println("Panier enregistré avec succès : " + panier); // Vérifiez que l'ID est affecté

        return mapToDtoGet(panier);
    }


    public PanierDtoGet getPanierByUserId(Long userId) {
        Panier panier = panierRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Panier non trouvé pour l'utilisateur"));
        return mapToDtoGet(panier);
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

        // Ajoutez d'autres mappages si nécessaire
        return dtoGet;
    }

    public List<Produit> getProduitsByPanierId(Long panierId) {
        List<PanierItem> panierItems = panierItemRepository.findByPanierId(panierId);
        return panierItems.stream()
                .map(PanierItem::getProduit)
                .collect(Collectors.toList());
    }
}
