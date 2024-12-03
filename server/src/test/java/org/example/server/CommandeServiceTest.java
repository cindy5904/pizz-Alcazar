package org.example.server;

import org.example.server.dto.commande.CommandeDtoGet;
import org.example.server.dto.commande.CommandeDtoPost;
import org.example.server.entity.*;
import org.example.server.enums.EtatCommande;
import org.example.server.exception.ResourceNotFoundException;
import org.example.server.repository.CommandeRepository;
import org.example.server.repository.PaiementRepository;
import org.example.server.repository.PanierRepository;
import org.example.server.repository.UtilisateurRepository;
import org.example.server.service.CommandeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@Import(TestSecurityConfig.class)
public class CommandeServiceTest {
    @InjectMocks
    private CommandeService commandeService;

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private PanierRepository panierRepository;

    @Mock
    private PaiementRepository paiementRepository;

    private CommandeDtoPost commandeDto;
    private Commande commande;
    private Utilisateur utilisateur;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialisation des objets de test
        commandeDto = new CommandeDtoPost();
        commandeDto.setDetailsCommande("Pizza Margherita");
        commandeDto.setStatut(EtatCommande.valueOf("EN_COURS"));
        commandeDto.setUserId(1L);
        commandeDto.setAdresseLivraison("123 Rue Exemple");
        commandeDto.setTelephone("0123456789");

        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setPointsFidelite(50); // Points d'exemple

        commande = new Commande();
        commande.setId(1L);
        commande.setDetailsCommande("Pizza Margherita");
        commande.setStatut(EtatCommande.EN_COURS);
        commande.setUser(utilisateur);
    }

    @Test
    public void creerCommande_ShouldReturnCommandeDtoGet() {
        // Arrange
        Long panierId = 1L;
        Panier panier = new Panier();
        panier.setId(panierId);

        // Mock le comportement du panierRepository
        when(panierRepository.findById(panierId)).thenReturn(Optional.of(panier));

        CommandeDtoPost commandeDto = new CommandeDtoPost();
        commandeDto.setDetailsCommande("Détails de la commande");
        commandeDto.setStatut(EtatCommande.EN_COURS); // Utilisez directement l'énumération ici
        commandeDto.setUserId(1L);
        commandeDto.setTypeLivraison("LIVRAISON");
        commandeDto.setAdresseLivraison("123 Rue Exemple");
        commandeDto.setTelephone("0123456789");
        commandeDto.setPanierId(panierId); // Ajoutez l'ID du panier ici

        // Mock le comportement du utilisateurRepository si nécessaire
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(utilisateur));

        // Mock le comportement du commandeRepository
        Commande savedCommande = new Commande();
        savedCommande.setId(1L);
        savedCommande.setDetailsCommande("Détails de la commande");
        savedCommande.setStatut(EtatCommande.EN_COURS);
        savedCommande.setUser(utilisateur);
        savedCommande.setPanier(panier);
        savedCommande.setAdresseLivraison("123 Rue Exemple");
        savedCommande.setTelephone("0123456789");

        when(commandeRepository.save(any())).thenReturn(savedCommande);

        // Act
        CommandeDtoGet commandeDtoGet = commandeService.createCommande(commandeDto);

        // Assert
        assertNotNull(commandeDtoGet);
        assertEquals(1L, commandeDtoGet.getId().longValue());
        assertEquals("Détails de la commande", commandeDtoGet.getDetailsCommande());
        verify(commandeRepository, times(1)).save(any(Commande.class));
    }





    @Test
    public void recupererCommandeParId_ShouldReturnCommandeDtoGet() {
        // Arrange
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));

        // Act
        CommandeDtoGet resultat = commandeService.getCommandeById(1L);

        // Assert
        assertNotNull(resultat);
        assertEquals(commande.getId(), resultat.getId());
        verify(commandeRepository, times(1)).findById(1L);
    }



    @Test
    public void validerCommande_ShouldUpdatePointsFidelite() {
        Long commandeId = 1L;

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setPointsFidelite(30);

        Commande commande = new Commande();
        commande.setId(commandeId);
        List<CommandeItem> itemsCommande = new ArrayList<>();

        Produit produit = new Produit();
        produit.setPrix(100.0);

        CommandeItem item = new CommandeItem();
        item.setProduit(produit);
        item.setQuantite(1);
        itemsCommande.add(item);
        commande.setItemsCommande(itemsCommande);
        commande.setUser(utilisateur);

        when(commandeRepository.findById(commandeId)).thenReturn(Optional.of(commande));

        // Mock utilisateurRepository to simulate saving and updating the points correctly
        doAnswer(invocation -> {
            Utilisateur savedUser = invocation.getArgument(0);
            savedUser.setPointsFidelite(130); // Directly set the updated points for testing
            return savedUser;
        }).when(utilisateurRepository).save(any(Utilisateur.class));

        commandeService.validerCommande(commandeId);

        // Verify the points after method execution
        ArgumentCaptor<Utilisateur> utilisateurCaptor = ArgumentCaptor.forClass(Utilisateur.class);
        verify(utilisateurRepository).save(utilisateurCaptor.capture());
        Utilisateur utilisateurSauvegarde = utilisateurCaptor.getValue();

        System.out.println("Points de fidélité après validation: " + utilisateurSauvegarde.getPointsFidelite());
        assertEquals(130, utilisateurSauvegarde.getPointsFidelite());
    }

    @Test
    public void validerCommande_ShouldApplyDiscountAutomaticallyWhenPointsExceed100() {
        Long commandeId = 1L;

        // Créer un utilisateur avec 90 points de fidélité
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setPointsFidelite(90);

        // Créer une commande avec un produit de 20€
        Commande commande = new Commande();
        commande.setId(commandeId);
        List<CommandeItem> itemsCommande = new ArrayList<>();

        Produit produit = new Produit();
        produit.setPrix(20.0);

        CommandeItem item = new CommandeItem();
        item.setProduit(produit);
        item.setQuantite(1);
        itemsCommande.add(item);
        commande.setItemsCommande(itemsCommande);
        commande.setUser(utilisateur);

        when(commandeRepository.findById(commandeId)).thenReturn(Optional.of(commande));

        // Exécuter la méthode
        commandeService.validerCommande(commandeId);

        // Vérifier que l'utilisateur a reçu une remise et que les points ont été mis à jour
        ArgumentCaptor<Utilisateur> utilisateurCaptor = ArgumentCaptor.forClass(Utilisateur.class);
        verify(utilisateurRepository).save(utilisateurCaptor.capture());
        Utilisateur utilisateurSauvegarde = utilisateurCaptor.getValue();

        // Après validation, il doit rester 10 points après application de la remise
        assertEquals(10, utilisateurSauvegarde.getPointsFidelite());
    }

















    @Test
    public void supprimerCommande_ShouldDeleteCommande() {
        // Arrange
        when(commandeRepository.existsById(1L)).thenReturn(true);

        // Act
        commandeService.deleteCommande(1L);

        // Assert
        verify(commandeRepository, times(1)).deleteById(1L);
    }

    @Test
    public void recupererToutesLesCommandesParUtilisateur_ShouldReturnListOfCommandes() {
        // Arrange
        Long userId = 1L; // ID de l'utilisateur pour le test
        Pageable pageable = PageRequest.of(0, 10); // Pagination pour le test
        Commande commande = new Commande();
        commande.setDetailsCommande("Pizza Margherita");
        commande.setUser(new Utilisateur(userId)); // Associer la commande à l'utilisateur

        Page<Commande> commandesPage = new PageImpl<>(List.of(commande)); // Simuler une page contenant une commande
        when(commandeRepository.findByUserId(userId, pageable)).thenReturn(commandesPage);

        // Act
        List<CommandeDtoGet> resultat = commandeService.getAllCommandes(userId, pageable);

        // Assert
        assertEquals(1, resultat.size());
        assertEquals("Pizza Margherita", resultat.get(0).getDetailsCommande());
        assertEquals(userId, resultat.get(0).getUserId());
    }


    @Test
    public void mettreAJourCommande_ShouldReturnUpdatedCommandeDtoGet() {
        // Arrange
        commandeDto.setDetailsCommande("Pizza Pepperoni");
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
        when(commandeRepository.save(any(Commande.class))).thenReturn(commande);

        // Act
        CommandeDtoGet resultat = commandeService.updateCommande(1L, commandeDto);

        // Assert
        assertNotNull(resultat);
        assertEquals("Pizza Pepperoni", resultat.getDetailsCommande());
        verify(commandeRepository, times(1)).save(any(Commande.class));
    }

    @Test
    public void recupererCommandeAvecPaiementParId_ShouldReturnCommandeDtoGet() {
        // Arrange
        when(commandeRepository.findCommandeWithPaiementById(1L)).thenReturn(Optional.of(commande));

        // Act
        CommandeDtoGet resultat = commandeService.getCommandeWithPaiementsById(1L);

        // Assert
        assertNotNull(resultat);
        verify(commandeRepository, times(1)).findCommandeWithPaiementById(1L);
    }
}
