package org.example.server;

import org.example.server.dto.commandeItem.CommandeItemDtoGet;
import org.example.server.dto.commandeItem.CommandeItemDtoPost;
import org.example.server.entity.Commande;
import org.example.server.entity.CommandeItem;
import org.example.server.entity.Produit;
import org.example.server.exception.ResourceNotFoundException;
import org.example.server.repository.CommandeItemRepository;
import org.example.server.repository.CommandeRepository;
import org.example.server.repository.ProduitRepository;
import org.example.server.service.CommandeItemService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@Import(TestSecurityConfig.class)
public class CommandeItemServiceTest {
    @InjectMocks
    private CommandeItemService commandeItemService;

    @Mock
    private CommandeItemRepository commandeItemRepository;

    @Mock
    private CommandeRepository commandeRepository;
    @Mock
    private ProduitRepository produitRepository;
    private CommandeItem commandeItem;
    private Commande commande;
    private Produit produit;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        commande = new Commande();
        commande.setId(1L); // Assurez-vous que l'ID est défini

        produit = new Produit();
        produit.setId(1L); // Assurez-vous que l'ID est défini
        // Vous pouvez également initialiser d'autres propriétés du produit ici

        commandeItem = new CommandeItem();
        commandeItem.setId(1L);
        commandeItem.setQuantite(2);
        commandeItem.setCommande(commande);
        commandeItem.setProduit(produit);

        // Configuration des mocks
        when(produitRepository.findById(produit.getId())).thenReturn(Optional.of(produit));
        when(produitRepository.existsById(produit.getId())).thenReturn(true);

        when(commandeItemRepository.existsById(commandeItem.getId())).thenReturn(true);
        when(commandeItemRepository.findById(commandeItem.getId())).thenReturn(Optional.of(commandeItem));
        when(commandeItemRepository.save(any(CommandeItem.class))).thenReturn(commandeItem);
    }

    @Test
    public void testCreateCommandeItem_Success() {
        CommandeItemDtoPost itemDto = new CommandeItemDtoPost();
        itemDto.setQuantite(2);
        itemDto.setProduitId(produit.getId());
        itemDto.setCommandeId(commande.getId());

        // Configurez les mocks
        lenient().when(commandeRepository.existsById(commande.getId())).thenReturn(true);
        when(commandeRepository.findById(commande.getId())).thenReturn(Optional.of(commande)); // Ajoutez cette ligne
        when(produitRepository.existsById(produit.getId())).thenReturn(true);
        when(produitRepository.findById(produit.getId())).thenReturn(Optional.of(produit)); // Ajoutez cette ligne
        when(commandeItemRepository.save(any(CommandeItem.class))).thenReturn(commandeItem);

        CommandeItemDtoGet createdItem = commandeItemService.createCommandeItem(itemDto);

        assertNotNull(createdItem);
        assertEquals(commandeItem.getId(), createdItem.getId());
        assertEquals(commandeItem.getQuantite(), createdItem.getQuantite());
        assertEquals(produit.getId(), createdItem.getProduitId());
    }


    @Test
    public void testCreateCommandeItem_CommandeNotFound() {
        CommandeItemDtoPost itemDto = new CommandeItemDtoPost();
        itemDto.setQuantite(2);

        // Simuler un produit qui existe
        Produit produitExistant = new Produit();
        produitExistant.setId(1L); // ID valide pour le produit
        when(produitRepository.existsById(produitExistant.getId())).thenReturn(true); // Simule que le produit existe
        lenient().when(produitRepository.findById(produitExistant.getId())).thenReturn(Optional.of(produitExistant));

        itemDto.setProduitId(produitExistant.getId()); // Produit valide
        itemDto.setCommandeId(2L); // Simuler une commande qui n'existe pas

        // Configurez le mock pour que findById retourne un Optional vide pour la commande
        when(commandeRepository.findById(2L)).thenReturn(Optional.empty());

        // Tester l'exception
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            commandeItemService.createCommandeItem(itemDto);
        });

        assertEquals("Commande not found", exception.getMessage()); // Vérifie que le message d'erreur est correct
    }



    @Test
    public void testCreateCommandeItem_ProduitNotFound() {
        CommandeItemDtoPost itemDto = new CommandeItemDtoPost();
        itemDto.setQuantite(2);
        itemDto.setProduitId(2L); // Simuler un produit qui n'existe pas
        itemDto.setCommandeId(commande.getId());

        lenient().when(commandeRepository.existsById(commande.getId())).thenReturn(true);
        when(produitRepository.existsById(2L)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            commandeItemService.createCommandeItem(itemDto);
        });

        assertEquals("Produit not found", exception.getMessage());
    }

    @Test
    public void testUpdateCommandeItem_Success() {
        CommandeItemDtoPost itemDto = new CommandeItemDtoPost();
        itemDto.setQuantite(3);
        itemDto.setProduitId(produit.getId()); // Utilise l'ID du produit dans le DTO
        itemDto.setCommandeId(commande.getId());

        // Le mock pour produitRepository a déjà été configuré dans setUp()

        // Appel de la méthode sous test
        CommandeItemDtoGet updatedItem = commandeItemService.updateCommandeItem(1L, itemDto);

        // Vérifier les résultats
        assertNotNull(updatedItem);
        assertEquals(commandeItem.getId(), updatedItem.getId());
        assertEquals(3, updatedItem.getQuantite()); // Vérifier la quantité mise à jour
        assertEquals(produit.getId(), updatedItem.getProduitId()); // Vérifier que le produit a été mis à jour
    }

    @Test
    public void testUpdateCommandeItem_NotFound() {
        CommandeItemDtoPost itemDto = new CommandeItemDtoPost();
        itemDto.setQuantite(3);
        itemDto.setProduitId(produit.getId());
        itemDto.setCommandeId(commande.getId());

        when(commandeItemRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            commandeItemService.updateCommandeItem(1L, itemDto);
        });

        assertEquals("CommandeItem not found with id: 1", exception.getMessage());
    }

    @Test
    public void testGetCommandeItemById_Success() {
        when(commandeItemRepository.findById(1L)).thenReturn(Optional.of(commandeItem));

        CommandeItemDtoGet foundItem = commandeItemService.getCommandeItemById(1L);

        assertNotNull(foundItem);
        assertEquals(commandeItem.getId(), foundItem.getId());
    }

    @Test
    public void testGetCommandeItemById_NotFound() {
        when(commandeItemRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            commandeItemService.getCommandeItemById(1L);
        });

        assertEquals("CommandeItem not found with id: 1", exception.getMessage());
    }

    @Test
    public void testGetItemsByCommandeId_Success() {
        when(commandeItemRepository.findByCommandeId(1L)).thenReturn(List.of(commandeItem));

        List<CommandeItemDtoGet> items = commandeItemService.getItemsByCommandeId(1L);

        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    public void testDeleteCommandeItem_Success() {
        when(commandeItemRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> commandeItemService.deleteCommandeItem(1L));
        verify(commandeItemRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteCommandeItem_NotFound() {
        when(commandeItemRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            commandeItemService.deleteCommandeItem(1L);
        });

        assertEquals("CommandeItem not found with id: 1", exception.getMessage());
    }

    @Test
    public void testExistsByProduitId() {
        when(commandeItemRepository.existsByProduitId(1L)).thenReturn(true);

        boolean exists = commandeItemService.existsByProduitId(1L);

        assertTrue(exists);
    }

    @Test
    public void testExistsByCommandeIdAndProduitId() {
        when(commandeItemRepository.existsByCommandeIdAndProduitId(1L, 1L)).thenReturn(true);

        boolean exists = commandeItemService.existsByCommandeIdAndProduitId(1L, 1L);

        assertTrue(exists);
    }

}
