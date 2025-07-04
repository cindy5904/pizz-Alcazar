package org.example.server.dto.historiqueFidelite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoriqueFideliteDtoGet {
    private Long id;
    private Long userId;
    private Long recompenseId;
    private LocalDateTime dateTransaction;
}
