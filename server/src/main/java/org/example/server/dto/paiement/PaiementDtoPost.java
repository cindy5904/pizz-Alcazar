package org.example.server.dto.paiement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaiementDtoPost {
    private double montant;
    private String statut;
    private String moyenPaiement;
    private String datePaiement;
    private Long commandeId;
}
