package org.example.server.dto.paiement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaiementDtoGet {
    private Long id;
    private double montant;
    private String statut;
    private String moyenPaiement;
    private LocalDateTime datePaiement;
    private Long commandeId;
}
