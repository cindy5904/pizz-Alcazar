package org.example.server.dto.commande;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.server.dto.commandeItem.CommandeItemDtoGet;
import org.example.server.dto.paiement.PaiementDtoGet;
import org.example.server.enums.EtatCommande;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommandeDtoGet {
    private Long id;
    private String detailsCommande;
    private EtatCommande statut;
    private String adresseLivraison;
    private String telephone;
    private String typeLivraison;
    private Long userId;    // L'ID de l'utilisateur qui a passé la commande
    private Long panierId;   // L'ID du panier lié à la commande

    private List<CommandeItemDtoGet> itemsCommande; // Liste des items de la commande
    private Long paiementId;
}
