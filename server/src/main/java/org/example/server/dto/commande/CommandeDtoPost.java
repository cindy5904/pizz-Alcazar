package org.example.server.dto.commande;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.server.enums.EtatCommande;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommandeDtoPost {
    private String detailsCommande; // Détails de la commande
    private EtatCommande statut;           // Statut de la commande (par exemple, "en cours", "livrée", etc.)
    private Long userId;            // ID de l'utilisateur qui passe la commande
    private String typeLivraison;    // Type de livraison : "livraison" ou "retrait"

    // Champs supplémentaires pour l'adresse et le téléphone si le type est "livraison"
    private String adresseLivraison; // Adresse pour la livraison
    private String telephone;
    private Long panierId;
}
