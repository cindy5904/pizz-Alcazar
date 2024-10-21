package org.example.server.dto.panierItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PanierItemDtoPost {
    private Long produitId;
    private int quantite;
}
