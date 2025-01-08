package org.example.server.service;

import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;
import jakarta.persistence.EntityNotFoundException;
import org.example.server.dto.commande.CommandeDtoGet;
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
import java.util.stream.Collectors;

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

        StatutPaiement statut;
        try {
            statut = StatutPaiement.valueOf(String.valueOf(dtoPost.getStatut()));
        } catch (IllegalArgumentException e) {
            statut = StatutPaiement.EN_ATTENTE;
        }
        paiement.setStatut(statut);

        paiement.setMoyenPaiement(dtoPost.getMoyenPaiement());
        paiement.setDatePaiement(LocalDateTime.now());

        Commande commande = commandeRepository.findById(dtoPost.getCommandeId())
                .orElseThrow(() -> new RuntimeException("Commande non trouvée pour l'ID : " + dtoPost.getCommandeId()));

        paiement.setCommande(commande);
        commande.setPaiement(paiement);

        Paiement savedPaiement = paiementRepository.save(paiement);
        commandeRepository.save(commande);

        System.out.println("Paiement enregistré : " + savedPaiement);

        if (statut == StatutPaiement.REUSSI) {
            Utilisateur utilisateur = commande.getUser();
            if (utilisateur != null) {
                System.out.println("Utilisateur trouvé : " + utilisateur.getId() + ", Points actuels : " + utilisateur.getPointsFidelite());

                double montantTotal = commande.getItemsCommande().stream()
                        .mapToDouble(item -> item.getProduit().getPrix() * item.getQuantite())
                        .sum();

                int pointsGagnes = (int) montantTotal;
                utilisateur.setPointsFidelite(utilisateur.getPointsFidelite() + pointsGagnes);
                utilisateurRepository.save(utilisateur);

                System.out.println("Points de fidélité ajoutés. Nouveau total : " + utilisateur.getPointsFidelite());

                int nombreDeRecompenses = utilisateur.getPointsFidelite() / 100;
                System.out.println("Nombre de récompenses à générer : " + nombreDeRecompenses);

                for (int i = 0; i < nombreDeRecompenses; i++) {
                    System.out.println("Génération de la récompense " + (i + 1) + " pour l'utilisateur : " + utilisateur.getId());
                    recompenseService.genererRecompensePourUtilisateur(utilisateur.getId());
                }

                utilisateur.setPointsFidelite(utilisateur.getPointsFidelite() % 100);
                utilisateurRepository.save(utilisateur);
                System.out.println("Points mis à jour après génération des récompenses : " + utilisateur.getPointsFidelite());
            }
        }

        return convertToDtoGet(savedPaiement);
    }



    private void ajouterPointsFidelite(Commande commande, double montant) {
        Utilisateur utilisateur = commande.getUser();
        if (utilisateur != null) {
            int pointsGagnes = (int) montant;
            utilisateur.setPointsFidelite(utilisateur.getPointsFidelite() + pointsGagnes);
            utilisateurRepository.save(utilisateur);


            while (utilisateur.getPointsFidelite() >= 100) {
                recompenseService.genererRecompensePourUtilisateur(utilisateur.getId());
                utilisateur = utilisateurRepository.findById(utilisateur.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé après mise à jour"));
            }
            utilisateurRepository.save(utilisateur);
            System.out.println("Points mis à jour : " + utilisateur.getPointsFidelite());
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
        // Log la commande ID
        System.out.println("Création de la commande PayPal pour la commande ID : " + commandeId);

        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée pour l'ID : " + commandeId));
        System.out.println("Commande trouvée : " + commande);

        double montantTotal = commande.getItemsCommande().stream()
                .mapToDouble(item -> item.getProduit().getPrix() * item.getQuantite())
                .sum();

        String montantFormate = String.format("%.2f", montantTotal).replace(",", ".");
        System.out.println("Montant total formaté : " + montantFormate);

        OrdersCreateInput ordersCreateInput = new OrdersCreateInput.Builder(
                null,
                new OrderRequest.Builder(
                        CheckoutPaymentIntent.CAPTURE,
                        Arrays.asList(
                                new PurchaseUnitRequest.Builder(
                                        new AmountWithBreakdown.Builder(
                                                "EUR",
                                                montantFormate
                                        ).build())
                                        .description("Commande de produits : ID " + commandeId)
                                        .referenceId("COMMANDE_" + commandeId)
                                        .build()))
                        .build())
                .build();

        OrdersController ordersController = client.getOrdersController();

        try {
            ApiResponse<Order> response = ordersController.ordersCreate(ordersCreateInput);
            Order result = response.getResult();
            String orderId = result.getId();
            System.out.println("Order ID créé : " + orderId);
            String approvalLink = result.getLinks().stream()
                    .filter(link -> "approve".equals(link.getRel())) // Cherche le lien "approve"
                    .map(link -> link.getHref())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Lien d'approbation introuvable."));
            System.out.println("Lien d'approbation PayPal : " + approvalLink);
            return approvalLink;

        } catch (ApiException e) {
            System.err.println("Erreur lors de l'appel à PayPal : " + e.getMessage());
            throw new RuntimeException("Erreur lors de la création de la commande PayPal", e);
        }
    }

    private Order captureOrderViaPayPal(String orderId) throws IOException, ApiException {
        OrdersCaptureInput ordersCaptureInput = new OrdersCaptureInput.Builder(orderId, null).build();
        OrdersController ordersController = client.getOrdersController();

        try {
            ApiResponse<Order> apiResponse = ordersController.ordersCapture(ordersCaptureInput);
            System.out.println("Réponse complète de l'API PayPal : " + apiResponse.getResult());
            return apiResponse.getResult();
        } catch (ApiException e) {
            System.err.println("Message d'erreur de l'API PayPal : " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause de l'erreur : " + e.getCause().getMessage());
            }
            throw e;
        }
    }



    public PaiementDtoGet capturePayPalOrder(String orderId, Long commandeId) throws Exception {
        System.out.println("Début de la capture pour l'Order ID : " + orderId + ", Commande ID : " + commandeId);

        try {
            // Capturer l'ordre via PayPal
            Order capturedOrder = captureOrderViaPayPal(orderId);
            System.out.println("Statut de l'Order après capture : " + capturedOrder.getStatus());

            if ("COMPLETED".equals(capturedOrder.getStatus())) {
                // Logique de sauvegarde du paiement et mise à jour de la commande
                Commande commande = commandeRepository.findById(commandeId)
                        .orElseThrow(() -> new RuntimeException("Commande non trouvée pour l'ID : " + commandeId));
                System.out.println("Commande après capture : " + commande);

                Paiement paiement = new Paiement();
                paiement.setMontant(commande.getItemsCommande().stream()
                        .mapToDouble(item -> item.getProduit().getPrix() * item.getQuantite())
                        .sum());
                paiement.setMoyenPaiement("PAYPAL");
                paiement.setStatut(StatutPaiement.REUSSI);
                paiement.setCommande(commande);
                paiement.setDatePaiement(LocalDateTime.now());
                Paiement savedPaiement = paiementRepository.save(paiement);
                System.out.println("Paiement sauvegardé : " + savedPaiement);

                commande.setStatut(EtatCommande.TERMINEE);
                commandeRepository.save(commande);
                System.out.println("Commande mise à jour : " + commande);

                if (commande.getUser() != null) {
                    ajouterPointsFidelite(commande, paiement.getMontant());
                    System.out.println("Points de fidélité ajoutés.");
                }

                return convertToDtoGet(savedPaiement);
            } else {
                System.err.println("Le paiement n'est pas terminé : " + capturedOrder.getStatus());
                throw new Exception("Le paiement n'est pas terminé");
            }
        } catch (ApiException e) {
            System.err.println("Erreur lors de la capture du paiement PayPal : " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Erreur lors de la capture du paiement PayPal", e);
        }
    }
    public List<CommandeDtoGet> getCommandesPayees() {
        List<Commande> commandes = paiementRepository.findCommandesByPaiementStatut(StatutPaiement.REUSSI);

        return commandes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    private CommandeDtoGet convertToDto(Commande commande) {
        CommandeDtoGet dto = new CommandeDtoGet();
        dto.setId(commande.getId());
        dto.setNumeroCommande(commande.getNumeroCommande());
        dto.setDetailsCommande(commande.getDetailsCommande());
        dto.setStatut(commande.getStatut());
        dto.setAdresseLivraison(commande.getAdresseLivraison());
        dto.setTelephone(commande.getTelephone());
        dto.setTypeLivraison(commande.getTypeLivraison());

        if (commande.getUser() != null) {
            dto.setUserId(commande.getUser().getId());
        }

        if (commande.getPanier() != null) {
            dto.setPanierId(commande.getPanier().getId());
        }

        if (commande.getPaiement() != null) {
            dto.setPaiementId(commande.getPaiement().getId());
        }

        return dto;
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


