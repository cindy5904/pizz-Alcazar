package org.example.server.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.transaction.Transactional;
import org.example.server.dto.commande.CommandeDtoGet;
import org.example.server.dto.paiement.PaiementDtoGet;
import org.example.server.dto.paiement.PaiementDtoPost;
import org.example.server.enums.StatutPaiement;
import org.example.server.service.PaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {
    @Autowired
    private PaiementService paiementService;
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping
    @Transactional
    public ResponseEntity<PaiementDtoGet> createPaiement(@RequestBody PaiementDtoPost dtoPost) {
        try {

            boolean isPaymentSuccessful = Math.random() > 0.2;
            StatutPaiement statut = isPaymentSuccessful ? StatutPaiement.REUSSI : StatutPaiement.ECHOUE;

            System.out.println("Creating payment for order: " + dtoPost.getCommandeId());

            PaiementDtoGet paiementDtoGet = paiementService.createPaiement(new PaiementDtoPost(
                    dtoPost.getMontant(),
                    statut,
                    dtoPost.getMoyenPaiement(),
                    LocalDateTime.now().toString(),
                    dtoPost.getCommandeId()
            ));

            if (isPaymentSuccessful) {
                return ResponseEntity.status(HttpStatus.CREATED).body(paiementDtoGet);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping("/paypal/create-order/{commandeId}")
    public ResponseEntity<String> createPayPalOrder(@PathVariable Long commandeId) {
        try {
            System.out.println("Creating PayPal order for order ID: " + commandeId);
            String orderId = paiementService.createPayPalOrder(commandeId);
            return ResponseEntity.ok(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); }
    }



    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping("/paypal/capture-order/{orderId}/{commandeId}")
    public ResponseEntity<PaiementDtoGet> capturePayPalOrder(
            @PathVariable String orderId,
            @PathVariable Long commandeId) {
        try {
            System.out.println("Capturing PayPal order: Order ID = " + orderId + ", Commande ID = " + commandeId);
            PaiementDtoGet paiementDto = paiementService.capturePayPalOrder(orderId, commandeId);
            return ResponseEntity.ok(paiementDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); }
    }



    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<PaiementDtoGet> getPaiementById(@PathVariable Long id) {
        PaiementDtoGet paiementDtoGet = paiementService.getPaiementById(id);
        return ResponseEntity.ok(paiementDtoGet);
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping("/commande/{commandeId}")
    public ResponseEntity<List<PaiementDtoGet>> getPaiementsByCommandeId(@PathVariable Long commandeId) {
        List<PaiementDtoGet> paiements = paiementService.getPaiementsByCommandeId(commandeId);
        return ResponseEntity.ok(paiements);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/commandes-payees")
    public ResponseEntity<List<CommandeDtoGet>> getCommandesPayees() {
        try {
            // Appel au service pour récupérer les commandes payées
            List<CommandeDtoGet> commandesPayees = paiementService.getCommandesPayees();

            if (commandesPayees.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(commandesPayees);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
