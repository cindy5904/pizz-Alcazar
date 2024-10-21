package org.example.server.dto.commandeItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommandeItemDtoPost {
    private int quantite;
    private Long produitId;
    private Long commandeId;
    private Long userId;
    private Long panierId;
}
