package org.example.server.dto.panier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.server.dto.panierItem.PanierItemDtoGet;
import org.example.server.dto.user.UtilisateurDtoGet;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PanierDtoGet {
    private Long id;
    private LocalDate dateCreation;
    private LocalDate dateModification;
    private UtilisateurDtoGet user;
    private List<PanierItemDtoGet> itemsPanier;
}
