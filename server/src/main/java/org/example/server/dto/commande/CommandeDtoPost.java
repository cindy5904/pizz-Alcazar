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
    private String detailsCommande;
    private EtatCommande statut;
    private Long userId;
    private String typeLivraison;
    private String adresseLivraison;
    private String telephone;
    private Long panierId;
    private Long paiementId;
}
