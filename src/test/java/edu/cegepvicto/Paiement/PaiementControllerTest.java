package edu.cegepvicto.Paiement;

import edu.cegepvicto.dimhortons.ConnectionFactory;
import edu.cegepvicto.dimhortons.Paiement.Commande;
import edu.cegepvicto.dimhortons.Paiement.LigneCommande;
import edu.cegepvicto.dimhortons.Paiement.CommandeDAO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'integration pour CommandeDao.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PaiementControllerTest {

    private CommandeDAO dao;

    @BeforeEach
    public void initialize() throws Exception {
        dao = new CommandeDAO();
        nettoyerDonneesTest();
        creerDonneesTest();
    }

    @AfterEach
    public void nettoyerApresChaqueTest() throws Exception {
        nettoyerDonneesTest();
    }

    /**
     * TI-CC-01 :
     */
    @Test
    @Order(1)
    public void enregistrerCommande_valide() throws SQLException {
        // Arrange
        Commande commande = new Commande();
        commande.setDateCommande(LocalDateTime.now());
        commande.setMontantTotal(10.00);
        commande.setStatut("PAYEE");

        LigneCommande ligne = new LigneCommande();
        ligne.setItemId(1);
        ligne.setNomItem("Café");
        ligne.setPrixUnitaire(2.50);
        ligne.setQuantite(4);

        commande.ajouterLigne(ligne);

        // Act
        int idCommande = dao.enregistrerCommande(commande, 1);

        // Assert
        assertTrue(idCommande > 0);
        assertNotNull(commande.getNumeroCommandeStr());
    }

    /**
     * TI-CC-02 :
     */
    @Test
    @Order(2)
    public void calculTaxesEtMontantFinal() throws SQLException {
        // Arrange
        Commande commande = new Commande();
        commande.setDateCommande(LocalDateTime.now());
        commande.setMontantTotal(100.00);
        commande.setStatut("PAYEE");

        LigneCommande ligne = new LigneCommande();
        ligne.setItemId(2);
        ligne.setNomItem("Muffin");
        ligne.setPrixUnitaire(100.00);
        ligne.setQuantite(1);
        commande.ajouterLigne(ligne);

        // Act
        dao.enregistrerCommande(commande, 1);

        // Assert
        assertEquals(14.975, commande.getTaxe(), 0.01);
        assertEquals(114.975, commande.getMontantFinal(), 0.01);
    }


    /**
     * TI-CC-03 :
     */
    @Test
    @Order(3)
    public void insertionLignesCommande() throws SQLException {
        // Arrange
        Commande commande = new Commande();
        commande.setDateCommande(LocalDateTime.now());
        commande.setMontantTotal(6.00);
        commande.setStatut("PAYEE");

        LigneCommande ligne1 = new LigneCommande();
        ligne1.setItemId(1);
        ligne1.setNomItem("Café");
        ligne1.setPrixUnitaire(3.00);
        ligne1.setQuantite(2);

        commande.ajouterLigne(ligne1);

        // Act
        int idCommande = dao.enregistrerCommande(commande, 1);

        // Assert DB
        String sql = "SELECT COUNT(*) FROM CommandeItem WHERE commande_id = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idCommande);
            ResultSet rs = ps.executeQuery();
            rs.next();

            assertEquals(1, rs.getInt(1));
        }
    }

    /**
     * TI-CC-04 :
     */
    @Test
    @Order(4)
    public void numeroCommandeEstGenere() throws SQLException {
        // Arrange
        Commande commande = new Commande();
        commande.setDateCommande(LocalDateTime.now());
        commande.setMontantTotal(4.00);
        commande.setStatut("PAYEE");

        LigneCommande ligne = new LigneCommande();
        ligne.setItemId(1);
        ligne.setNomItem("Café");
        ligne.setPrixUnitaire(4.00);
        ligne.setQuantite(1);

        commande.ajouterLigne(ligne);

        // Act
        dao.enregistrerCommande(commande, 1);

        // Assert
        assertNotNull(commande.getNumeroCommandeStr());
        assertTrue(commande.getNumeroCommandeStr().startsWith("CMD-"));
    }

    private void creerDonneesTest() throws SQLException {
        // Insertion des données de test nécessaires
        String sqlInsert = "INSERT INTO Commande (id, numero_commande, statut, date_commande, utilisateur_id) VALUES (?, ?, ?, NOW(), ?)";
        try (Connection connexion = ConnectionFactory.getConnection()) {
            try (PreparedStatement requete = connexion.prepareStatement(sqlInsert)) {
                requete.setInt(1, 9999);
                requete.setString(2, "TEST_001");
                requete.setString(3, "EN_ATTENTE");
                requete.setInt(4, 1);
                requete.executeUpdate();

                requete.setInt(1, 10000);
                requete.setString(2, "TEST_002");
                requete.setString(3, "EN_PREPARATION");
                requete.setInt(4, 1);
                requete.executeUpdate();

                requete.setInt(1, 10001);
                requete.setString(2, "TEST_003");
                requete.setString(3, "EN_PREPARATION");
                requete.setInt(4, 1);
                requete.executeUpdate();
            } catch (Exception exception) {
                System.out.println("Erreur lors de l'insertion des données de test : " + exception.getMessage());
            }
        }
    }
    private void nettoyerDonneesTest() throws SQLException {
        // Suppression des données de test créées
        String sqlDelete = "DELETE FROM Commande WHERE numero_commande LIKE 'TEST_%'";
        try (Connection connexion = ConnectionFactory.getConnection()) {
            try (PreparedStatement requete = connexion.prepareStatement(sqlDelete)) {
                requete.executeUpdate();
            } catch (Exception exception) {
                System.out.println("Erreur lors de la suppression des données de test : " + exception.getMessage());
            }

        }
    }

    @AfterAll
    static void nettoyerApresTests() throws SQLException {
        // Nettoyage final après tous les tests
        String sqlDelete = "DELETE FROM Commande WHERE numero_commande LIKE 'TEST_%'";
        try (Connection connexion = ConnectionFactory.getConnection()) {
            try (PreparedStatement requete = connexion.prepareStatement(sqlDelete)) {
                requete.executeUpdate();
            } catch (Exception exception) {
                System.out.println("Erreur lors du nettoyage final des données de test : " + exception.getMessage());
            }
        }
    }
}