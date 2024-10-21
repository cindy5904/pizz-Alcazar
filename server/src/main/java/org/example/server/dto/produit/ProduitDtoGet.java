package org.example.server.dto.produit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProduitDtoGet {
    private Long id;
    private String nom;
    private String description;
    private double prix;
    private boolean disponibilite;
    private String categorieNom;
}
