package org.example.server.dto.recompense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecompenseDtoGet {
    private Long id;
    private String nom;
    private String description;
    private int pointsNecessaires;
    private LocalDate dateRemise;
    private String codeRemise;
}
