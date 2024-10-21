package org.example.server.service;

import jakarta.persistence.EntityNotFoundException;
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

    private PanierItemDtoGet mapToDtoGet(PanierItem panierItem) {
        PanierItemDtoGet dto = new PanierItemDtoGet();
        dto.setId(panierItem.getId());
        dto.setQuantite(panierItem.getQuantite());

        // Si tu as un DTO pour Produit, fais la conversion ici
        ProduitDtoGet produitDto = new ProduitDtoGet();
        produitDto.setId(panierItem.getProduit().getId());
        produitDto.setNom(panierItem.getProduit().getNom());
        dto.setProduit(produitDto);

        return dto;
    }


    // Ajouter un item ou mettre à jour sa quantité
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
            // Si l'item n'existe pas, on le crée et l'ajoute au panier
            panierItem = new PanierItem();
            panierItem.setProduit(produit);
            panierItem.setQuantite(itemDto.getQuantite());
            panierItem.setPanier(panier);
        }

        panierItemRepository.save(panierItem);

        return mapToDtoGet(panierItem);
    }
    public PanierItemDtoGet reduireQuantiteItem(Long produitId, Long panierId, int quantite) {
        PanierItem panierItem = panierItemRepository.findByProduitIdAndPanierId(produitId, panierId);

        if (panierItem == null) {
            throw new EntityNotFoundException("Produit non trouvé dans le panier");
        }

        int nouvelleQuantite = panierItem.getQuantite() - quantite;
        if (nouvelleQuantite <= 0) {
            // Si la nouvelle quantité est <= 0, on supprime l'item du panier
            panierItemRepository.deleteByProduitIdAndPanierId(produitId, panierId);
            return null; // Ou renvoyer une réponse indiquant que l'item a été supprimé
        } else {
            panierItem.setQuantite(nouvelleQuantite);
            panierItemRepository.save(panierItem);
        }

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
