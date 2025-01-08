package org.example.server;

import jakarta.persistence.EntityNotFoundException;
import org.example.server.dto.commande.CommandeDtoGet;
import org.example.server.dto.commande.CommandeDtoPost;
import org.example.server.dto.commandeItem.CommandeItemDtoGet;
import org.example.server.dto.commandeItem.CommandeItemDtoPost;
import org.example.server.entity.*;
import org.example.server.enums.EtatCommande;
import org.example.server.exception.ResourceNotFoundException;
import org.example.server.repository.*;
import org.example.server.service.CommandeItemService;
import org.example.server.service.CommandeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@Import(TestSecurityConfig.class)
public class CommandeServiceTest {
    @InjectMocks
    private CommandeItemService commandeItemService;

    @Mock
    private CommandeItemRepository commandeItemRepository;
    @Mock
    private PaiementRepository paiementRepository;

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private ProduitRepository produitRepository;

    private Commande commande;
    private Produit produit;
    private CommandeItem commandeItem;
    private CommandeItemDtoPost commandeItemDtoPost;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialisation du produit
        produit = new Produit();
        produit.setId(1L);
        produit.setNom("Produit Test");
        produit.setPrix(50.0);

        // Initialisation de la commande
        commande = new Commande();
        commande.setId(1L);
        commande.setDetailsCommande("Détails de la commande");

        // Initialisation du CommandeItem
        commandeItem = new CommandeItem();
        commandeItem.setId(1L);
        commandeItem.setQuantite(2);
        commandeItem.setProduit(produit);
        commandeItem.setCommande(commande);

        // Initialisation du CommandeItemDtoPost
        commandeItemDtoPost = new CommandeItemDtoPost();
        commandeItemDtoPost.setProduitId(1L);
        commandeItemDtoPost.setCommandeId(1L);
        commandeItemDtoPost.setQuantite(2);
    }

    @Test
    public void testCreateCommandeItem_Success() {
        // Arrange
        when(produitRepository.existsById(1L)).thenReturn(true);
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeItemRepository.save(any(CommandeItem.class))).thenReturn(commandeItem);

        // Act
        CommandeItemDtoGet result = commandeItemService.createCommandeItem(commandeItemDtoPost);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getProduitId().longValue());
        assertEquals(2, result.getQuantite());
        verify(commandeItemRepository, times(1)).save(any(CommandeItem.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testCreateCommandeItem_ProduitNotFound() {
        // Arrange
        when(produitRepository.existsById(1L)).thenReturn(false);

        // Act
        commandeItemService.createCommandeItem(commandeItemDtoPost);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testCreateCommandeItem_CommandeNotFound() {
        // Arrange
        when(produitRepository.existsById(1L)).thenReturn(true);
        when(commandeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        commandeItemService.createCommandeItem(commandeItemDtoPost);
    }

    @Test
    public void testUpdateCommandeItem_Success() {
        // Arrange
        when(commandeItemRepository.findById(1L)).thenReturn(Optional.of(commandeItem));
        when(produitRepository.existsById(1L)).thenReturn(true);
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(commandeItemRepository.save(any(CommandeItem.class))).thenReturn(commandeItem);

        // Act
        CommandeItemDtoGet result = commandeItemService.updateCommandeItem(1L, commandeItemDtoPost);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getQuantite());
        verify(commandeItemRepository, times(1)).save(any(CommandeItem.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateCommandeItem_NotFound() {
        // Arrange
        when(commandeItemRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        commandeItemService.updateCommandeItem(1L, commandeItemDtoPost);
    }

    @Test
    public void testGetCommandeItemById_Success() {
        // Arrange
        when(commandeItemRepository.findById(1L)).thenReturn(Optional.of(commandeItem));

        // Act
        CommandeItemDtoGet result = commandeItemService.getCommandeItemById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getProduitId().longValue());
        verify(commandeItemRepository, times(1)).findById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetCommandeItemById_NotFound() {
        // Arrange
        when(commandeItemRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        commandeItemService.getCommandeItemById(1L);
    }

    @Test
    public void testDeleteCommandeItem_Success() {
        // Arrange
        when(commandeItemRepository.existsById(1L)).thenReturn(true);

        // Act
        commandeItemService.deleteCommandeItem(1L);

        // Assert
        verify(commandeItemRepository, times(1)).deleteById(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteCommandeItem_NotFound() {
        // Arrange
        when(commandeItemRepository.existsById(1L)).thenReturn(false);

        // Act
        commandeItemService.deleteCommandeItem(1L);
    }

    @Test
    public void testGetItemsByCommandeId_Success() {
        // Arrange
        List<CommandeItem> items = Arrays.asList(commandeItem);
        when(commandeItemRepository.findByCommandeId(1L)).thenReturn(items);

        // Act
        List<CommandeItemDtoGet> result = commandeItemService.getItemsByCommandeId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commandeItemRepository, times(1)).findByCommandeId(1L);
    }
    @Test
    public void testFindCommandesByDatePaiementBetween() {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 7, 23, 59);

        List<Commande> commandes = commandeRepository.findCommandesByDatePaiementBetween(startDate, endDate);

        System.out.println("Commandes trouvées : " + commandes.size());
        commandes.forEach(c -> System.out.println("Commande ID : " + c.getId()));
    }
    @Test
    public void testFindCommandesByPaiementDate() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 5, 0, 0);

        Commande commande1 = new Commande();
        commande1.setId(79L);
        commande1.setDetailsCommande("Commande de Test");
        commande1.setStatut(EtatCommande.valueOf("EN_COURS"));

        Paiement paiement1 = new Paiement();
        paiement1.setId(32L);
        paiement1.setDatePaiement(LocalDateTime.of(2025, 1, 6, 15, 0));
        paiement1.setCommande(commande1);

        List<Commande> expectedCommandes = List.of(commande1);

        Mockito.when(paiementRepository.findCommandesByPaiementDate(startDate))
                .thenReturn(expectedCommandes);

        // Act
        List<Commande> commandes = paiementRepository.findCommandesByPaiementDate(startDate);

        // Assert
        Assertions.assertFalse(commandes.isEmpty(), "Commandes trouvées : " + commandes.size());
        Assertions.assertEquals(1, commandes.size());
        Assertions.assertEquals("Commande de Test", commandes.get(0).getDetailsCommande());
    }

}

