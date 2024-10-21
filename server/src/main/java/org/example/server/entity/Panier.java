package org.example.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "paniers")
public class Panier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    @OneToOne
    @JoinColumn(name = "user_id")
    private Utilisateur user;
    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL)
    private List<PanierItem> itemsPanier;
    @OneToOne(mappedBy = "panier")
    private Commande commande;
}
