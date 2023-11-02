package tn.esprit.rh.achat;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.rh.achat.entities.*;
import tn.esprit.rh.achat.repositories.*;
import tn.esprit.rh.achat.services.FactureServiceImpl;
import tn.esprit.rh.achat.services.ReglementServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class FactureServiceImplTest  {
    @Mock
    FactureRepository factureRepository;

    @Mock
    OperateurRepository operateurRepository;

    @Mock
    FournisseurRepository fournisseurRepository;


    @Mock
    ReglementServiceImpl reglementService;

    @InjectMocks
    FactureServiceImpl factureService;

    private Facture facture;
    private Operateur operateur;

    @BeforeEach
    void setUp() {
        operateur = new Operateur();
        operateur.setIdOperateur(1L);
        operateur.setNom("John");
        operateur.setPrenom("Doe");

        facture = new Facture();
        facture.setIdFacture(1L);
        facture.setMontantRemise(0.0f);
        facture.setMontantFacture(100.0f);

        // Initialize date properties
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            facture.setDateCreationFacture(dateFormat.parse("2023-10-24"));
            facture.setDateDerniereModificationFacture(dateFormat.parse("2023-10-24"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        facture.setArchivee(false);

        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setIdFournisseur(1L);
        fournisseur.setCode("testfourn");
        fournisseur.setLibelle("Test Fournisseur");

        facture.setFournisseur(fournisseur);

        Set<DetailFacture> detailsFacture = new HashSet<>();
        DetailFacture detailFacture = new DetailFacture();
        detailFacture.setIdDetailFacture(1L);
        detailFacture.setQteCommandee(10);
        detailFacture.setPrixTotalDetail(50.0f);
        detailFacture.setPourcentageRemise(10);
        detailFacture.setMontantRemise(5.0f);

        Produit produit = new Produit();
        produit.setIdProduit(1L);
        produit.setCodeProduit("testprod");
        produit.setLibelleProduit("test Product");
        produit.setPrix(50.0f);

        detailFacture.setProduit(produit);
        detailFacture.setFacture(facture);

        detailsFacture.add(detailFacture);

        facture.setDetailsFacture(detailsFacture);
    }

    @Test
    void retrieveAllFacturesTest() {
        // Create a list using ArrayList
        List<Facture> facturesList = new ArrayList<>();
        facturesList.add(facture);

        // Mock the behavior of factureRepository.findAll()
        when(factureRepository.findAll()).thenReturn(facturesList);

        // Call the method under test
        List<Facture> retrievedFacturesList = factureService.retrieveAllFactures();

        // Use assertThat(actual).hasSize(expected)
        assertThat(retrievedFacturesList).hasSize(1);
        verify(factureRepository).findAll();
    }

    @Test
    void addFactureTest() {
        // Mock the behavior of factureRepository.save()
        when(factureRepository.save(Mockito.any(Facture.class))).thenReturn(facture);

        // Call the method under test
        Facture savedFacture = factureService.addFacture(facture);

        // Assertions
        assertThat(savedFacture).isNotNull();
        verify(factureRepository).save(Mockito.any(Facture.class));
    }

    @Test
    @Disabled
    void addDetailsFactureTest() {
        // Create a Facture with an empty set of DetailFacture
        Facture facture = new Facture();
        facture.setDetailsFacture(new HashSet<>());

        // Create a DetailFacture and add it to the set
        DetailFacture detailFacture = new DetailFacture();
        detailFacture.setQteCommandee(5);
        detailFacture.setPourcentageRemise(10);

        Produit produit = new Produit();
        produit.setPrix(50.0f);

        detailFacture.setProduit(produit);

        facture.getDetailsFacture().add(detailFacture);

        // Calculate the expected montantFacture and montantRemise
        float expectedMontantFacture = (5 * 50.0f) - ((5 * 50.0f * 10) / 100);
        float expectedMontantRemise = (5 * 50.0f * 10) / 100;

        // Call the method under test
        Facture updatedFacture = factureService.addDetailsFacture(facture, facture.getDetailsFacture());

        // Assertions
        assertThat(updatedFacture).isNotNull();
        assertThat(updatedFacture.getMontantFacture()).isEqualTo(expectedMontantFacture);
        assertThat(updatedFacture.getMontantRemise()).isEqualTo(expectedMontantRemise);
    }

    @Test
    void cancelFactureTest() {
        Long factureId = 1L;
        // Mock the behavior of factureRepository.findById() and factureRepository.updateFacture()
        when(factureRepository.findById(factureId)).thenReturn(Optional.of(facture));

        // Call the method under test
        factureService.cancelFacture(factureId);

        // Assertions or verifications as needed
        verify(factureRepository).findById(factureId);
        verify(factureRepository).updateFacture(factureId);
    }

    @Test
    void retrieveFactureTest() {
        Long factureId = 1L;
        // Mock the behavior of factureRepository.findById()
        when(factureRepository.findById(factureId)).thenReturn(Optional.of(facture));

        // Call the method under test
        Facture retrievedFacture = factureService.retrieveFacture(factureId);

        // Assertions
        assertThat(retrievedFacture).isNotNull();
        verify(factureRepository).findById(factureId);
    }

    @Test
    void getFacturesByFournisseurTest() {
        Long idFournisseur = 1L;
        // Mock the behavior of fournisseurRepository.findById()
        when(fournisseurRepository.findById(idFournisseur)).thenReturn(Optional.of(new Fournisseur()));

        // Call the method under test
        List<Facture> factures = factureService.getFacturesByFournisseur(idFournisseur);

        // Assertions or verifications as needed
        verify(fournisseurRepository).findById(idFournisseur);
    }

    @Test
    @Disabled
    void assignOperateurToFactureTest() {
        Long idOperateur = 1L;
        Long idFacture = 1L;
        // Mock the behavior of operateurRepository.findById() and factureRepository.findById()
        when(operateurRepository.findById(idOperateur)).thenReturn(Optional.of(operateur));
        when(factureRepository.findById(idFacture)).thenReturn(Optional.of(facture));

        // Call the method under test
        factureService.assignOperateurToFacture(idOperateur, idFacture);

        // Assertions or verifications as needed
        verify(operateurRepository).save(Mockito.any(Operateur.class));
    }

    @Test
    void pourcentageRecouvrementTest() {
        Date startDate = new Date();
        Date endDate = new Date();

        // Mock the behavior of factureRepository.getTotalFacturesEntreDeuxDates()
        when(factureRepository.getTotalFacturesEntreDeuxDates(startDate, endDate)).thenReturn(100.0f);
        // Mock the behavior of reglementService.getChiffreAffaireEntreDeuxDate()
        when(reglementService.getChiffreAffaireEntreDeuxDate(startDate, endDate)).thenReturn(80.0f);

        // Call the method under test
        float pourcentage = factureService.pourcentageRecouvrement(startDate, endDate);

        // Assertions or verifications as needed
        assertThat(pourcentage).isEqualTo(80.0f);
    }
}