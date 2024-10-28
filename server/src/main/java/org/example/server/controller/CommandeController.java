package org.example.server.controller;

import org.example.server.dto.commande.CommandeDtoGet;
import org.example.server.dto.commande.CommandeDtoPost;
import org.example.server.service.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {
    @Autowired
    private CommandeService commandeService;

    @PostMapping
    public ResponseEntity<CommandeDtoGet> createCommande(@RequestBody CommandeDtoPost commandeDto) {
        CommandeDtoGet createdCommande = commandeService.createCommande(commandeDto);
        return new ResponseEntity<>(createdCommande, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeDtoGet> getCommandeById(@PathVariable Long id) {
        CommandeDtoGet commande = commandeService.getCommandeById(id);
        return ResponseEntity.ok(commande);
    }

    @GetMapping
    public ResponseEntity<List<CommandeDtoGet>> getAllCommandes() {
        List<CommandeDtoGet> commandes = commandeService.getAllCommandes();
        return ResponseEntity.ok(commandes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommandeDtoGet> updateCommande(@PathVariable Long id, @RequestBody CommandeDtoPost commandeDto) {
        CommandeDtoGet updatedCommande = commandeService.updateCommande(id, commandeDto);
        return ResponseEntity.ok(updatedCommande);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable Long id) {
        commandeService.deleteCommande(id);
        return ResponseEntity.noContent().build();
    }
}
