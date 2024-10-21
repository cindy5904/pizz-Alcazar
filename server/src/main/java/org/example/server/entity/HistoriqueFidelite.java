package org.example.server.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "historiquesFidelites")
public class HistoriqueFidelite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dateTransaction;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Utilisateur user;
    @ManyToOne
    @JoinColumn(name = "recompense_id", nullable = true)
    private Recompense recompense;
}
