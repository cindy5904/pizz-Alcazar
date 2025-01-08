package org.example.server.controller;

import org.example.server.dto.commande.CommandeDtoGet;
import org.example.server.dto.commande.CommandeDtoPost;
import org.example.server.entity.Commande;
import org.example.server.repository.PaiementRepository;
import org.example.server.service.CommandeService;
import org.example.server.service.PaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {
    @Autowired
    private CommandeService commandeService;


    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping
    public ResponseEntity<CommandeDtoGet> createCommande(@RequestBody CommandeDtoPost commandeDto) {
        CommandeDtoGet createdCommande = commandeService.createCommande(commandeDto);
        return new ResponseEntity<>(createdCommande, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<CommandeDtoGet> getCommandeById(@PathVariable Long id) {
        CommandeDtoGet commande = commandeService.getCommandeById(id);
        return ResponseEntity.ok(commande);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public ResponseEntity<Page<CommandeDtoGet>> getAllCommandes(
            @RequestParam(required = true) Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        System.out.println("Méthode getAllCommandes appelée pour userId: " + userId);
        Page<CommandeDtoGet> commandes = (Page<CommandeDtoGet>) commandeService.getAllCommandes(userId, pageable);
        return ResponseEntity.ok(commandes);
    }




    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PutMapping("/{id}")
    public ResponseEntity<CommandeDtoGet> updateCommande(@PathVariable Long id, @RequestBody CommandeDtoPost commandeDto) {
        CommandeDtoGet updatedCommande = commandeService.updateCommande(id, commandeDto);
        return ResponseEntity.ok(updatedCommande);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable Long id) {
        commandeService.deleteCommande(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/par-semaine")
    public ResponseEntity<List<CommandeDtoGet>> getCommandesParSemaine(@RequestParam("dateDebut") String dateDebut) {
        try {
            LocalDate startDate = LocalDate.parse(dateDebut);

            // Appel du service pour récupérer les commandes en DTO
            List<CommandeDtoGet> commandes = commandeService.getCommandesParSemaine(startDate);

            if (commandes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(commandes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/statut")
    public ResponseEntity<CommandeDtoGet> updateStatutCommande(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String nouveauStatut = request.get("statut");
        System.out.println("Reçu statut : " + nouveauStatut);
        CommandeDtoGet updatedCommande = commandeService.updateCommandeStatut(id, nouveauStatut);
        System.out.println("Commande mise à jour : " + updatedCommande);
        return ResponseEntity.ok(updatedCommande);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/commandes-payees-et-en-cours")
    public ResponseEntity<List<CommandeDtoGet>> getCommandesPayeesEtEnCours() {
        List<CommandeDtoGet> commandes = commandeService.getCommandesPayeesEtEnCours();
        return ResponseEntity.ok(commandes);
    }

}
