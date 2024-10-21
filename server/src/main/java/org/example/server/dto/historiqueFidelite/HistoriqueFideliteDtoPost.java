package org.example.server.dto.historiqueFidelite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoriqueFideliteDtoPost {
    private Long userId;
    private Long recompenseId;
    private String dateTransaction;
}
