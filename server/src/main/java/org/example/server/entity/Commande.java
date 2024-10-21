package org.example.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.server.enums.EtatCommande;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "commandes")
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String detailsCommande;
    @Enumerated(EnumType.STRING)
    private EtatCommande statut;
    private String adresseLivraison;
    private String telephone;
    private String typeLivraison;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Utilisateur user;
    @OneToOne
    @JoinColumn(name = "paiement_id")
    private Paiement paiement;
    @OneToOne
    @JoinColumn(name = "panier_id")
    private Panier panier;
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommandeItem> itemsCommande;


}
