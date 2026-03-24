package edu.cegepvicto.Cuisine;

import edu.cegepvicto.dimhortons.ConnectionFactory;
import edu.cegepvicto.dimhortons.cuisine.modeles.CommandeCuisine;
import edu.cegepvicto.dimhortons.cuisine.DAO.CommandeDao;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'integration pour CommandeDao.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommandeDAOTest {

    private CommandeDao dao;

    @BeforeEach
    public void initialize() throws Exception {
        dao = new CommandeDao();
        nettoyerDonneesTest();
        creerDonneesTest();
    }

    @AfterEach
    public void nettoyerApresChaqueTest() throws Exception {
        nettoyerDonneesTest();
    }

    /**
     * TI-CC-01 : chargement initial
     */
    @Test
    @Order(1)
    public void chargementInitial() {
        List<CommandeCuisine> commandes = dao.lireCommandesActives();

        assertNotNull(commandes);
        assertTrue(commandes.size() > 0);
    }

    /**
     * TI-CC-02 : transition attente vers preparation
     */
    @Test
    @Order(2)
    public void transitionAttentePreparation() {
        dao.mettreEtatCommande(9999, CommandeCuisine.EtatCommande.EN_ATTENTE, CommandeCuisine.EtatCommande.EN_PREPARATION);

        List<CommandeCuisine> commandes = dao.lireCommandesActives();
        CommandeCuisine commande = commandes.stream()
                .filter(cmd -> cmd.getIdCommande() == 9999)
                .findFirst()
                .orElse(null);

        assertNotNull(commande);
        assertEquals(CommandeCuisine.EtatCommande.EN_PREPARATION, commande.getEtatCommande());
    }

    /**
     * TI-CC-03 : transition preparation vers prete
     */
    @Test
    @Order(3)
    public void transitionPreparationPrete() {
        dao.mettreEtatCommande(10000, CommandeCuisine.EtatCommande.EN_PREPARATION, CommandeCuisine.EtatCommande.PRETE);

        List<CommandeCuisine> commandes = dao.lireCommandesActives();
        CommandeCuisine commande = commandes.stream()
                .filter(cmd -> cmd.getIdCommande() == 10000)
                .findFirst()
                .orElse(null);

        assertNotNull(commande);
        assertEquals(CommandeCuisine.EtatCommande.PRETE, commande.getEtatCommande());
    }

    /**
     * TI-CC-04 : annulation commande
     */
    @Test
    @Order(4)
    public void annulationCommande() {
        dao.annulerCommande(10001);

        List<CommandeCuisine> commandes = dao.lireCommandesActives();
        CommandeCuisine commande = commandes.stream()
                .filter(cmd -> cmd.getIdCommande() == 10001)
                .findFirst()
                .orElse(null);

        assertNull(commande);
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