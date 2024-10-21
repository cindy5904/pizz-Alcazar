package org.example.server.dto.commandeItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommandeItemDtoGet {
    private Long id;
    private int quantite;
    private Long produitId;
    private String produitNom;
    private double produitPrix;
    private Long commandeId;

}
