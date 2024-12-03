package org.example.server.controller;

import org.example.server.dto.commande.CommandeDtoGet;
import org.example.server.dto.commande.CommandeDtoPost;
import org.example.server.service.CommandeService;
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

import java.util.List;

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
}
