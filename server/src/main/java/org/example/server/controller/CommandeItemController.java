package org.example.server.controller;

import org.example.server.dto.commandeItem.CommandeItemDtoGet;
import org.example.server.dto.commandeItem.CommandeItemDtoPost;
import org.example.server.service.CommandeItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/commande-items")
public class CommandeItemController {
    @Autowired
    private CommandeItemService commandeItemService;


    @PostMapping
    public ResponseEntity<CommandeItemDtoGet> createCommandeItem(@RequestBody CommandeItemDtoPost itemDto) {
        CommandeItemDtoGet createdItem = commandeItemService.createCommandeItem(itemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }


    @PutMapping("/{id}")
    public ResponseEntity<CommandeItemDtoGet> updateCommandeItem(
            @PathVariable Long id,
            @RequestBody CommandeItemDtoPost itemDto) {
        CommandeItemDtoGet updatedItem = commandeItemService.updateCommandeItem(id, itemDto);
        return ResponseEntity.ok(updatedItem);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CommandeItemDtoGet> getCommandeItemById(@PathVariable Long id) {
        CommandeItemDtoGet item = commandeItemService.getCommandeItemById(id);
        return ResponseEntity.ok(item);
    }

    // Endpoint pour obtenir les éléments de commande par ID de commande
    @GetMapping("/commande/{commandeId}")
    public ResponseEntity<List<CommandeItemDtoGet>> getItemsByCommandeId(@PathVariable Long commandeId) {
        List<CommandeItemDtoGet> items = commandeItemService.getItemsByCommandeId(commandeId);
        return ResponseEntity.ok(items);
    }

    // Endpoint pour supprimer un élément de commande par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommandeItem(@PathVariable Long id) {
        commandeItemService.deleteCommandeItem(id);
        return ResponseEntity.noContent().build();
    }
}
