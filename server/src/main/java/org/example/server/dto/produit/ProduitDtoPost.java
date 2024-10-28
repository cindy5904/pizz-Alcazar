package org.example.server.dto.produit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProduitDtoPost {
    private String nom;
    private String description;
    private double prix;
    private boolean disponibilite;
    private Long categorieId;
    private String imagePath;
}
