package org.example.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
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
    private boolean actif;
    @OneToOne
    @JoinColumn(name = "user_id")
    private Utilisateur user;
    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL)
    private List<PanierItem> itemsPanier;
    @OneToOne(mappedBy = "panier")
    private Commande commande;
}
