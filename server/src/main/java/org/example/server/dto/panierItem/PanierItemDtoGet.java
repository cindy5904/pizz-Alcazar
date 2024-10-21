package org.example.server.dto.panierItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.server.dto.produit.ProduitDtoGet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PanierItemDtoGet {
    private Long id;
    private ProduitDtoGet produit;
    private int quantite;
}
