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
    private CommandeRepository commandeRepository; // Ajoute le repository pour Commande

    @Autowired
    private ProduitRepository produitRepository;

    public CommandeItemDtoGet createCommandeItem(CommandeItemDtoPost itemDto) {
        // Vérifie si la commande existe
        if (itemDto.getProduitId() == null) {
            throw new ResourceNotFoundException("Produit not found");
        }

        // Vérifie si le produit existe
        if (!produitRepository.existsById(itemDto.getProduitId())) {
            throw new ResourceNotFoundException("Produit not found");
        }

        // Crée un nouvel objet CommandeItem
        CommandeItem commandeItem = new CommandeItem();
        commandeItem.setQuantite(itemDto.getQuantite());

        // Récupère la commande associée si nécessaire
        if (itemDto.getCommandeId() != null) {
            Commande commande = commandeRepository.findById(itemDto.getCommandeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Commande not found"));
            commandeItem.setCommande(commande);
        }

        // Associe le produit au CommandeItem
        Produit produit = produitRepository.findById(itemDto.getProduitId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit not found"));
        commandeItem.setProduit(produit);

        // Enregistre le CommandeItem dans la base de données
        CommandeItem savedItem = commandeItemRepository.save(commandeItem);

        // Convertit et retourne le CommandeItem en DTO
        return convertToDto(savedItem);
    }


    // Méthode pour mettre à jour un CommandeItem
    public CommandeItemDtoGet updateCommandeItem(Long id, CommandeItemDtoPost itemDto) {
        if (!commandeItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("CommandeItem not found with id: " + id);
        }

        CommandeItem commandeItem = commandeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CommandeItem not found with id: " + id));

        // Vérifie la disponibilité du produit
        if (itemDto.getProduitId() != null) {
            if (!produitRepository.existsById(itemDto.getProduitId())) {
                throw new ResourceNotFoundException("Produit not found");
            }
            Produit produit = produitRepository.findById(itemDto.getProduitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit not found"));
            commandeItem.setProduit(produit);
        }

        // Met à jour la quantité
        commandeItem.setQuantite(itemDto.getQuantite());

        // Enregistre les modifications
        CommandeItem updatedItem = commandeItemRepository.save(commandeItem);

        // Convertit et retourne le CommandeItem en DTO
        return convertToDto(updatedItem);
    }

    // Méthode pour obtenir un CommandeItem par ID
    public CommandeItemDtoGet getCommandeItemById(Long id) {
        CommandeItem commandeItem = commandeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CommandeItem not found with id: " + id));
        return convertToDto(commandeItem);
    }

    // Méthode pour obtenir les éléments par ID de commande
    public List<CommandeItemDtoGet> getItemsByCommandeId(Long commandeId) {
        List<CommandeItem> items = commandeItemRepository.findByCommandeId(commandeId);
        return items.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Méthode pour supprimer un CommandeItem par ID
    public void deleteCommandeItem(Long id) {
        if (!commandeItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("CommandeItem not found with id: " + id);
        }
        commandeItemRepository.deleteById(id);
    }

    // Vérifie si un produit existe
    public boolean existsByProduitId(Long produitId) {
        return commandeItemRepository.existsByProduitId(produitId);
    }

    // Vérifie si une commande contient un produit
    public boolean existsByCommandeIdAndProduitId(Long commandeId, Long produitId) {
        return commandeItemRepository.existsByCommandeIdAndProduitId(commandeId, produitId);
    }

    // Convertit CommandeItem en CommandeItemDtoGet
    private CommandeItemDtoGet convertToDto(CommandeItem commandeItem) {
        CommandeItemDtoGet dto = new CommandeItemDtoGet();
        dto.setId(commandeItem.getId());
        dto.setQuantite(commandeItem.getQuantite());
        dto.setProduitId(commandeItem.getProduit().getId());
        dto.setProduitNom(commandeItem.getProduit().getNom());
        dto.setProduitPrix(commandeItem.getProduit().getPrix());

        // Ajouter l'ID de la commande si nécessaire
        if (commandeItem.getCommande() != null) {
            dto.setCommandeId(commandeItem.getCommande().getId());
        }

        return dto;
    }

}
