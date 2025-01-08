package org.example.server.dto.paiement;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
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
