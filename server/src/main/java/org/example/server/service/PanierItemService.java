package org.example.server.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.server.dto.panier.PanierDtoGet;
import org.example.server.dto.panierItem.PanierItemDtoGet;
import org.example.server.dto.panierItem.PanierItemDtoPost;
import org.example.server.dto.produit.ProduitDtoGet;
import org.example.server.entity.Panier;
import org.example.server.entity.PanierItem;
import org.example.server.entity.Produit;
import org.example.server.repository.PanierItemRepository;
import org.example.server.repository.PanierRepository;
import org.example.server.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PanierItemService {
    @Autowired
    private PanierItemRepository panierItemRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private EntityManager entityManager;

    private PanierItemDtoGet mapToDtoGet(PanierItem panierItem) {
        PanierItemDtoGet dto = new PanierItemDtoGet();
        dto.setId(panierItem.getId());
        dto.setQuantite(panierItem.getQuantite());

        ProduitDtoGet produitDto = new ProduitDtoGet();
        produitDto.setId(panierItem.getProduit().getId());
        produitDto.setNom(panierItem.getProduit().getNom());
        dto.setProduit(produitDto);

        return dto;
    }



    public PanierItemDtoGet ajouterOuMettreAJourItem(PanierItemDtoPost itemDto, Long panierId) {
        Panier panier = panierRepository.findById(panierId)
                .orElseThrow(() -> new EntityNotFoundException("Panier non trouvé"));

        Produit produit = produitRepository.findById(itemDto.getProduitId())
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé"));

        PanierItem panierItem = panierItemRepository.findByProduitIdAndPanierId(itemDto.getProduitId(), panierId);

        if (panierItem != null) {
            // Si l'item existe déjà dans le panier, on met à jour la quantité
            panierItem.setQuantite(panierItem.getQuantite() + itemDto.getQuantite());
        } else {
            panierItem = new PanierItem();
            panierItem.setProduit(produit);
            panierItem.setQuantite(itemDto.getQuantite());
            panierItem.setPanier(panier);
        }

        panierItemRepository.save(panierItem);
        System.out.println("Item ajouté ou mis à jour : " + panierItem);

        return mapToDtoGet(panierItem);
    }
    @Transactional
    public PanierItemDtoGet reduireQuantiteItem(Long produitId, Long panierId, int reduction) {
        System.out.println("Réduction de quantité pour produitId : " + produitId + ", panierId : " + panierId + ", réduction : " + reduction);

        PanierItem panierItem = panierItemRepository.findByProduitIdAndPanierId(produitId, panierId);

        if (panierItem == null) {
            System.out.println("Produit non trouvé dans le panier !");
            throw new EntityNotFoundException("Produit non trouvé dans le panier");
        }

        // Appliquer la réduction
        int nouvelleQuantite = panierItem.getQuantite() - reduction;
        System.out.println("Nouvelle quantité : " + nouvelleQuantite);

        if (nouvelleQuantite <= 0) {
            panierItemRepository.delete(panierItem);
            System.out.println("Produit supprimé du panier car quantité <= 0");
            return null;
        }

        panierItem.setQuantite(nouvelleQuantite);
        panierItemRepository.save(panierItem);

        return mapToDtoGet(panierItem);
    }







    public void supprimerItem(Long produitId, Long panierId) {
        PanierItem panierItem = panierItemRepository.findByProduitIdAndPanierId(produitId, panierId);
        if (panierItem != null) {
            panierItemRepository.delete(panierItem);
        } else {
            throw new EntityNotFoundException("Produit non trouvé dans le panier");
        }
    }


}
