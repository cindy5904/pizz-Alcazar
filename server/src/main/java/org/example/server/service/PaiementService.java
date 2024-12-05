package org.example.server.service;

import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;
import jakarta.persistence.EntityNotFoundException;
import org.example.server.dto.paiement.PaiementDtoGet;
import org.example.server.dto.paiement.PaiementDtoPost;
import org.example.server.entity.Commande;
import org.example.server.entity.Paiement;
import org.example.server.entity.Utilisateur;
import org.example.server.enums.EtatCommande;
import org.example.server.enums.StatutPaiement;
import org.example.server.repository.CommandeRepository;
import org.example.server.repository.PaiementRepository;
import org.example.server.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PaiementService {
    @Autowired
    private PaiementRepository paiementRepository;

    private final PaypalServerSdkClient client;

    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private RecompenseService recompenseService;

    public PaiementService(PaypalServerSdkClient client) {
        this.client = client;
    }


    @Transactional
    public PaiementDtoGet createPaiement(PaiementDtoPost dtoPost) {
        Paiement paiement = new Paiement();
        paiement.setMontant(dtoPost.getMontant());

        // Définir le statut ou une valeur par défaut
        StatutPaiement statut;
        try {
            statut = StatutPaiement.valueOf(dtoPost.getStatut());
        } catch (IllegalArgumentException e) {
            statut = StatutPaiement.EN_ATTENTE;
        }
        paiement.setStatut(statut);

        paiement.setMoyenPaiement(dtoPost.getMoyenPaiement());
        paiement.setDatePaiement(LocalDateTime.now());

        // Assurez-vous que la commande existe avant de l'ajouter
        Commande commande = commandeRepository.findById(dtoPost.getCommandeId())
                .orElseThrow(() -> new RuntimeException("Commande non trouvée pour l'ID : " + dtoPost.getCommandeId()));
        paiement.setCommande(commande);

        Paiement savedPaiement = paiementRepository.save(paiement);

        // Ajouter les points de fidélité si le paiement est validé
        if (statut == StatutPaiement.REUSSI) {
            Utilisateur utilisateur = commande.getUser();
            if (utilisateur != null) {
                // Calculer le montant total de la commande
                double montantTotal = commande.getItemsCommande().stream()
                        .mapToDouble(item -> item.getProduit().getPrix() * item.getQuantite())
                        .sum();

                // Ajouter les points de fidélité (1€ = 1 point)
                int pointsGagnes = (int) montantTotal;
                utilisateur.setPointsFidelite(utilisateur.getPointsFidelite() + pointsGagnes);
                utilisateurRepository.save(utilisateur);

                // Vérifier si les points atteignent ou dépassent 100
                while (utilisateur.getPointsFidelite() >= 100) {
                    recompenseService.genererRecompensePourUtilisateur(utilisateur.getId());
                    utilisateur = utilisateurRepository.findById(utilisateur.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé après mise à jour"));
                }

                System.out.println("Points mis à jour : " + utilisateur.getPointsFidelite());
            }
        }

        return convertToDtoGet(savedPaiement);
    }




    // Méthode pour ajouter des points de fidélité
    private void ajouterPointsFidelite(Commande commande, double montant) {
        Utilisateur utilisateur = commande.getUser();
        if (utilisateur != null) {
            int pointsGagnes = (int) montant;
            utilisateur.setPointsFidelite(utilisateur.getPointsFidelite() + pointsGagnes);
            utilisateurRepository.save(utilisateur);

            // Vérifier si les points atteignent ou dépassent 100
            while (utilisateur.getPointsFidelite() >= 100) {
                recompenseService.genererRecompensePourUtilisateur(utilisateur.getId());
                utilisateur = utilisateurRepository.findById(utilisateur.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé après mise à jour"));
            }
        }
    }


    public PaiementDtoGet getPaiementById(Long id) {
        Optional<Paiement> paiementOptional = paiementRepository.findById(id);
        if (paiementOptional.isPresent()) {
            return convertToDtoGet(paiementOptional.get());
        } else {
            throw new RuntimeException("Paiement non trouvé pour l'ID : " + id);
        }
    }

    public List<PaiementDtoGet> getPaiementsByCommandeId(Long commandeId) {
        List<Paiement> paiements = paiementRepository.findByCommandeId(commandeId);
        return paiements.stream().map(this::convertToDtoGet).toList();
    }
    public boolean validerDetailsBancaires(String numCarte, String titulaireCarte, String dateExpiration, String cvv) {
        // Vérifier si le numéro de carte commence par "4" (exemple fictif)
        if (!numCarte.startsWith("4") || numCarte.length() != 16) {
            return false;
        }
        // Vérifier si le CVV a exactement 3 chiffres
        if (cvv.length() != 3 || !cvv.matches("\\d{3}")) {
            return false;
        }
        // Vérifier le format de la date d'expiration "MM/YY"
        if (!dateExpiration.matches("\\d{2}/\\d{2}")) {
            return false;
        }
        return true;
    }
    public String createPayPalOrder(Long commandeId) throws Exception {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée pour l'ID : " + commandeId));

        double montantTotal = commande.getItemsCommande().stream()
                .mapToDouble(item -> item.getProduit().getPrix() * item.getQuantite())
                .sum();

        // Corrigez ici le montant
        String montantFormate = String.format("%.2f", montantTotal).replace(",", ".");
        System.out.println("Montant total formaté : " + montantFormate);

        OrdersCreateInput ordersCreateInput = new OrdersCreateInput.Builder(
                null,
                new OrderRequest.Builder(
                        CheckoutPaymentIntent.CAPTURE,
                        Arrays.asList(
                                new PurchaseUnitRequest.Builder(
                                        new AmountWithBreakdown.Builder(
                                                "EUR", // Devise
                                                montantFormate // Montant corrigé
                                        ).build()
                                ).build()
                        )
                ).build()
        ).build();

        OrdersController ordersController = client.getOrdersController();

        try {
            ApiResponse<Order> response = ordersController.ordersCreate(ordersCreateInput);
            String orderId = response.getResult().getId();
            System.out.println("Commande PayPal créée avec succès. Order ID : " + orderId);
            System.out.println("Réponse complète de PayPal : " + response.getResult());
            return orderId;
        } catch (ApiException e) {
            System.err.println("Erreur lors de l'appel à PayPal : " + e.getMessage());
            throw new RuntimeException("Erreur lors de la création de la commande PayPal", e);
        }
    }
    private Order captureOrderViaPayPal(String orderId) throws IOException, ApiException {
        OrdersCaptureInput ordersCaptureInput = new OrdersCaptureInput.Builder(orderId, null).build();
        OrdersController ordersController = client.getOrdersController();

        ApiResponse<Order> apiResponse = ordersController.ordersCapture(ordersCaptureInput);
        System.out.println("Commande capturée avec succès via PayPal : " + apiResponse.getResult());
        return apiResponse.getResult(); // Retourne l'objet Order capturé
    }


    public PaiementDtoGet capturePayPalOrder(String orderId, Long commandeId) throws Exception {
        System.out.println("Début de la capture pour l'Order ID : " + orderId + ", Commande ID : " + commandeId);

        try {
            // Capturer l'ordre via PayPal
            Order capturedOrder = captureOrderViaPayPal(orderId);

            if ("COMPLETED".equals(capturedOrder.getStatus())) {
                // Récupérer la commande associée dans la base de données
                Commande commande = commandeRepository.findById(commandeId)
                        .orElseThrow(() -> new RuntimeException("Commande non trouvée pour l'ID : " + commandeId));

                // Créer un nouveau paiement dans la base de données
                Paiement paiement = new Paiement();
                paiement.setMontant(commande.getItemsCommande().stream()
                        .mapToDouble(item -> item.getProduit().getPrix() * item.getQuantite())
                        .sum());
                paiement.setMoyenPaiement("PAYPAL");
                paiement.setStatut(StatutPaiement.REUSSI);
                paiement.setCommande(commande);
                paiement.setDatePaiement(LocalDateTime.now());
                Paiement savedPaiement = paiementRepository.save(paiement);
                System.out.println("Paiement enregistré : " + savedPaiement.getId());

                // Mettre à jour le statut de la commande
                commande.setStatut(EtatCommande.TERMINEE);
                commandeRepository.save(commande);
                System.out.println("Statut de la commande mis à jour à TERMINEE");

                // Ajouter des points de fidélité
                if (commande.getUser() != null) {
                    ajouterPointsFidelite(commande, paiement.getMontant());
                    System.out.println("Points de fidélité ajoutés.");
                }

                return convertToDtoGet(savedPaiement); // Retourne le DTO de paiement
            } else {
                System.err.println("Le paiement n'est pas terminé : " + capturedOrder.getStatus());
                throw new Exception("Le paiement n'est pas terminé");
            }
        } catch (ApiException e) {
            // Gestion des erreurs
            System.err.println("Erreur lors de la capture du paiement PayPal : " + e.getMessage());
            e.printStackTrace(); // Log complet pour diagnostiquer
            throw new Exception("Erreur lors de la capture du paiement PayPal", e);
        }

    }









    private PaiementDtoGet convertToDtoGet(Paiement paiement) {
        PaiementDtoGet dtoGet = new PaiementDtoGet();
        dtoGet.setId(paiement.getId());
        dtoGet.setMontant(paiement.getMontant());
        dtoGet.setStatut(paiement.getStatut().toString());
        dtoGet.setMoyenPaiement(paiement.getMoyenPaiement());
        dtoGet.setDatePaiement(paiement.getDatePaiement());
        dtoGet.setCommandeId(paiement.getCommande() != null ? paiement.getCommande().getId() : null);
        return dtoGet;
    }
}


