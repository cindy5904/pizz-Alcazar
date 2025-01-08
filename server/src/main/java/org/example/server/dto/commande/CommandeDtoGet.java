package org.example.server.dto.commande;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.server.dto.commandeItem.CommandeItemDtoGet;
import org.example.server.dto.paiement.PaiementDtoGet;
import org.example.server.dto.panier.PanierDtoGet;
import org.example.server.dto.user.UtilisateurDtoGet;
import org.example.server.enums.EtatCommande;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommandeDtoGet {
    private Long id;
    private String numeroCommande;
    private String detailsCommande;
    private EtatCommande statut;
    private String adresseLivraison;
    private String telephone;
    private String typeLivraison;
    private Long userId;
    private Long panierId;
    private UtilisateurDtoGet user;
    private PanierDtoGet panier;

    private List<CommandeItemDtoGet> itemsCommande;
    private Long paiementId;
}
