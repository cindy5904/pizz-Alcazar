package org.example.server.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.transaction.Transactional;
import org.example.server.dto.paiement.PaiementDtoGet;
import org.example.server.dto.paiement.PaiementDtoPost;
import org.example.server.service.PaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {
    @Autowired
    private PaiementService paiementService;

    // Endpoint pour créer un paiement
    @PostMapping
    @Transactional
    public ResponseEntity<PaiementDtoGet> createPaiement(@RequestBody PaiementDtoPost dtoPost) {
        try {
            // Créez un PaymentIntent sur Stripe
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) dtoPost.getMontant()) // Montant en cents
                    .setCurrency("eur") // Ou la devise souhaitée
                    .setPaymentMethod(dtoPost.getMoyenPaiement()) // ID du moyen de paiement
                    .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                    .setConfirm(true) // Confirmez le paiement
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Enregistrer le paiement dans la base de données
            PaiementDtoGet paiementDtoGet = paiementService.createPaiement(new PaiementDtoPost(
                    paymentIntent.getAmount(),
                    paymentIntent.getStatus(),
                    paymentIntent.getId(), // ID du PaymentIntent
                    LocalDateTime.now().toString(), // Date actuelle
                    dtoPost.getCommandeId()
            ));

            return ResponseEntity.status(HttpStatus.CREATED).body(paiementDtoGet);

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Endpoint pour récupérer un paiement par son ID
    @GetMapping("/{id}")
    public ResponseEntity<PaiementDtoGet> getPaiementById(@PathVariable Long id) {
        PaiementDtoGet paiementDtoGet = paiementService.getPaiementById(id);
        return ResponseEntity.ok(paiementDtoGet);
    }

    // Endpoint pour récupérer tous les paiements d'une commande
    @GetMapping("/commande/{commandeId}")
    public ResponseEntity<List<PaiementDtoGet>> getPaiementsByCommandeId(@PathVariable Long commandeId) {
        List<PaiementDtoGet> paiements = paiementService.getPaiementsByCommandeId(commandeId);
        return ResponseEntity.ok(paiements);
    }
}
