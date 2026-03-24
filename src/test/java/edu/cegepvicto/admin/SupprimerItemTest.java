package edu.cegepvicto.admin;

import edu.cegepvicto.dimhortons.ConnectionFactory;
import edu.cegepvicto.dimhortons.Menu.Item;
import edu.cegepvicto.dimhortons.Menu.ItemDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests du CU Supprimer un item du menu.
 * Auteur : Rachid
 *
 */
class SupprimerItemTest {

    private ItemDAO dao;

    @BeforeEach
    void setup() {
        dao = new ItemDAO();
    }

    // TEST 1 : La liste d’items doit exister (écran suppression = liste à afficher)
    @Test
    void testChargerListeItems_nonVide() throws Exception {
        List<Item> items = dao.findAll();

        assertNotNull(items);
        assertTrue(items.size() > 0, "La BD doit contenir au moins 1 item");
    }

    // TEST 2 : Suppression nominale (item temporaire)
    @Test
    void testSuppressionItemTemp_succes() throws Exception {
        int categorieId = creerCategorieTemp();
        int itemId = creerItemTemp(categorieId, "ITEM_TEST_DELETE_OK");

        assertTrue(existeItem(itemId), "L'item temp doit exister avant suppression");

        dao.delete(itemId);

        assertFalse(existeItem(itemId), "L'item temp doit être supprimé");
    }

    // TEST 3 : Suppression refusée si item lié à CommandeItem
    @Test
    void testSuppressionItemLieCommande_refuse() throws Exception {
        int itemIdLieCommande = trouverUnItemLieACommande();

        assertTrue(itemIdLieCommande > 0,
                "Aucun item lié à une commande trouvé. Ajoute un seed CommandeItem pour ce test.");

        boolean exceptionLevee = false;
        try {
            dao.delete(itemIdLieCommande);
        } catch (Exception e) {
            exceptionLevee = true;
        }

        // attendu : soit exception, soit item toujours présent
        assertTrue(exceptionLevee || existeItem(itemIdLieCommande),
                "La suppression doit être refusée OU l’item doit rester en BD.");
    }

    // TEST 4 : ID inexistant
    @Test
    void testSuppressionIdInexistant_aucunEffet() throws Exception {
        int idInexistant = 99999999;

        boolean exceptionLevee = false;
        try {
            dao.delete(idInexistant);
        } catch (Exception e) {
            exceptionLevee = true;
        }

        assertTrue(exceptionLevee || !existeItem(idInexistant),
                "ID inexistant : ne doit pas supprimer autre chose.");
    }


    private int creerCategorieTemp() throws SQLException {
        String sql = "INSERT INTO CategorieItem(nom, description) VALUES (?, ?)";
        try (Connection cnx = ConnectionFactory.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, "CAT_TEST_DELETE_" + System.currentTimeMillis());
            ps.setString(2, "Catégorie temporaire pour tests suppression");
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private int creerItemTemp(int categorieId, String nom) throws SQLException {
        String sql = "INSERT INTO Item(nom, description, prix, categorie_id, image_url, calories) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cnx = ConnectionFactory.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nom);
            ps.setString(2, "Item temporaire pour test suppression admin");
            ps.setDouble(3, 1.00);
            ps.setInt(4, categorieId);
            ps.setString(5, null);
            ps.setInt(6, 0);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private boolean existeItem(int itemId) throws SQLException {
        String sql = "SELECT id FROM Item WHERE id = ?";
        try (Connection cnx = ConnectionFactory.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int trouverUnItemLieACommande() throws SQLException {
        String sql = "SELECT item_id FROM CommandeItem LIMIT 1";
        try (Connection cnx = ConnectionFactory.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt("item_id");
        }
        return -1;
    }
}
