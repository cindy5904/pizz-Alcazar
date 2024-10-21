package org.example.server.dto.recompense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecompenseDtoPost {
    private String nom;
    private String description;
    private int pointsNecessaires;
}
