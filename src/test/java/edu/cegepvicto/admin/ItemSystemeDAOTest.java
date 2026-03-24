package edu.cegepvicto.admin;

import edu.cegepvicto.dimhortons.Admin.modeles.ItemSysteme;
import edu.cegepvicto.dimhortons.Admin.DAO.ItemSystemeDAO;
import edu.cegepvicto.dimhortons.ConnectionFactory;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'integration pour ItemSystemeDAO.
 * Utilise des données de test specifiques pour mes tests.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemSystemeDAOTest {

    private ItemSystemeDAO dao;

    // IDs des items créés pendant les tests
    private static int idItemNominal = -1;
    private static int idItemPrixMin = -1;
    private static int idItemPrixMax = -1;


    @BeforeEach
    void initialize() throws Exception {
        dao = new ItemSystemeDAO();
    }

    /**
     * TU-CI-01 : creerItem nominal
     */
    @Test
    @Order(1)
    void casNominal() {
        ItemSysteme item = new ItemSysteme(
                "TEST_Cafe_nominal",
                "Delicieux cafe de test",
                3.49,
                1,
                null,
                "/edu/cegepvicto/dimhortons/images/timglace.png",
                true
        );

        int resultat = dao.creerItemSysteme(item);

        if (resultat > 0) {
            idItemNominal = resultat; // on garde l'ID pour le cleanup
        }
        else {
            System.out.println("Echec de la creation de l'item test");
        }
        assertTrue(resultat > 0, "L'item devrait etre cree avec un ID positif");
    }

    /**
     * TU-CI-02 : prix minimum
     */
    @Test
    @Order(2)
    void prixMinimum() {
        ItemSysteme item = new ItemSysteme(
                "TEST_Cafe_prix_min",
                "Item test prix minimum",
                0.01,
                1,
                null,
                "/edu/cegepvicto/dimhortons/images/timglace.png",
                true
        );

        int resultat = dao.creerItemSysteme(item);

        if (resultat > 0) {
            idItemPrixMin = resultat;
        }
        assertTrue(resultat > 0, "Le prix minimum devrait etre accepte");
    }

    /**
     * TU-CI-03 : prix maximum
     */
    @Test
    @Order(3)
    void prixMaximum() {
        ItemSysteme item = new ItemSysteme(
                "TEST_Cafe_prix_max",
                "Item test prix maximum",
                999.99,
                1,
                null,
                "/edu/cegepvicto/dimhortons/images/timglace.png",
                true
        );

        int resultat = dao.creerItemSysteme(item);

        if (resultat > 0) {
            idItemPrixMax = resultat;
        }
        assertTrue(resultat > 0, "Le prix maximum devrait etre accepte");
    }

    /**
     * TU-CI-04 : nom vide
     */
    @Test
    @Order(4)
    void nomVide() {
        ItemSysteme item = new ItemSysteme(
                "", // nom vide pour tester la validation
                "Item test",
                3.00,
                1,
                null,
                "/edu/cegepvicto/dimhortons/images/timglace.png",
                true
        );

        assertThrows(IllegalArgumentException.class, () -> dao.creerItemSysteme(item),
                "Un nom vide devrait lever une IllegalArgumentException");
    }

    /**
     * Nettoie toutes les données de test après tous les tests
     */
    @AfterAll
    static void nettoyerDonneesTest() throws Exception {

        try (Connection connexion = ConnectionFactory.getConnection()) {
            // Supprime les items crees pendant les tests par ID
            if (idItemNominal > 0 || idItemPrixMin > 0 || idItemPrixMax > 0) {
                String sqlDelete = "DELETE FROM item WHERE id IN (?, ?, ?)";
                try (PreparedStatement requete = connexion.prepareStatement(sqlDelete)) {
                    requete.setInt(1, idItemNominal);
                    requete.setInt(2, idItemPrixMin);
                    requete.setInt(3, idItemPrixMax);
                    requete.executeUpdate();
                }
            }
        } catch (Exception exception) {
            System.err.println("Erreur lors du nettoyage: " + exception.getMessage());
        }
    }
}