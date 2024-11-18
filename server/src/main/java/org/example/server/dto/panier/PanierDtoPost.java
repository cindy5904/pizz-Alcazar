package org.example.server.dto.panier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.server.dto.panierItem.PanierItemDtoPost;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PanierDtoPost {
    private Long userId;
    private boolean actif;
    private List<PanierItemDtoPost> itemsPanier;
}
