package org.example.server.service;

import org.example.server.dto.commandeItem.CommandeItemDtoGet;
import org.example.server.dto.commandeItem.CommandeItemDtoPost;
import org.example.server.entity.Commande;
import org.example.server.entity.CommandeItem;
import org.example.server.entity.Produit;
import org.example.server.exception.ResourceNotFoundException;
import org.example.server.repository.CommandeItemRepository;
import org.example.server.repository.CommandeRepository;
import org.example.server.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommandeItemService {
    @Autowired
    private CommandeItemRepository commandeItemRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private ProduitRepository produitRepository;

    public CommandeItemDtoGet createCommandeItem(CommandeItemDtoPost itemDto) {
        // Validation des entrées
        if (itemDto.getProduitId() == null) {
            throw new ResourceNotFoundException("Produit non trouvé");
        }

        if (!produitRepository.existsById(itemDto.getProduitId())) {
            throw new ResourceNotFoundException("Produit non trouvé");
        }

        // Vérification de la commande associée
        Commande commande = null;
        if (itemDto.getCommandeId() != null) {
            commande = commandeRepository.findById(itemDto.getCommandeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));
        }

        // Vérification de l'unicité du produit dans la commande
        if (commande != null && existsByCommandeIdAndProduitId(commande.getId(), itemDto.getProduitId())) {
            throw new IllegalArgumentException("Produit déjà présent dans cette commande");
        }

        // Création d'un nouvel objet CommandeItem
        CommandeItem commandeItem = new CommandeItem();
        commandeItem.setQuantite(itemDto.getQuantite());
        commandeItem.setCommande(commande);
        commandeItem.setProduit(produitRepository.findById(itemDto.getProduitId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé")));

        // Enregistrement dans la base de données
        CommandeItem savedItem = commandeItemRepository.save(commandeItem);

        return convertToDto(savedItem);
    }

    public CommandeItemDtoGet updateCommandeItem(Long id, CommandeItemDtoPost itemDto) {
        // Vérification de l'existence de CommandeItem
        CommandeItem commandeItem = commandeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CommandeItem non trouvé avec l'id : " + id));

        // Vérification de la disponibilité du produit
        if (itemDto.getProduitId() != null) {
            if (!produitRepository.existsById(itemDto.getProduitId())) {
                throw new ResourceNotFoundException("Produit non trouvé");
            }
            commandeItem.setProduit(produitRepository.findById(itemDto.getProduitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé")));
        }

        commandeItem.setQuantite(itemDto.getQuantite());
        CommandeItem updatedItem = commandeItemRepository.save(commandeItem);
        return convertToDto(updatedItem);
    }

    public CommandeItemDtoGet getCommandeItemById(Long id) {
        CommandeItem commandeItem = commandeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CommandeItem non trouvé avec l'id : " + id));
        return convertToDto(commandeItem);
    }

    public List<CommandeItemDtoGet> getItemsByCommandeId(Long commandeId) {
        List<CommandeItem> items = commandeItemRepository.findByCommandeId(commandeId);
        return items.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public void deleteCommandeItem(Long id) {
        if (!commandeItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("CommandeItem non trouvé avec l'id : " + id);
        }
        commandeItemRepository.deleteById(id);
    }

    public boolean existsByProduitId(Long produitId) {
        return commandeItemRepository.existsByProduitId(produitId);
    }

    public boolean existsByCommandeIdAndProduitId(Long commandeId, Long produitId) {
        return commandeItemRepository.existsByCommandeIdAndProduitId(commandeId, produitId);
    }

    private CommandeItemDtoGet convertToDto(CommandeItem commandeItem) {
        CommandeItemDtoGet dto = new CommandeItemDtoGet();
        dto.setId(commandeItem.getId());
        dto.setQuantite(commandeItem.getQuantite());
        dto.setProduitId(commandeItem.getProduit().getId());
        dto.setProduitNom(commandeItem.getProduit().getNom());
        dto.setProduitPrix(commandeItem.getProduit().getPrix());

        if (commandeItem.getCommande() != null) {
            dto.setCommandeId(commandeItem.getCommande().getId());
        }

        return dto;
    }

}
