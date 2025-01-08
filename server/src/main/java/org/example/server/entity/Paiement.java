package org.example.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.server.enums.StatutPaiement;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "paiements")
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double montant;
    @Enumerated(EnumType.STRING)
    private StatutPaiement statut;
    private String moyenPaiement;
    private LocalDateTime datePaiement;
    @OneToOne(mappedBy = "paiement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Commande commande;
}
