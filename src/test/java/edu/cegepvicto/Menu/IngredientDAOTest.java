package edu.cegepvicto.Menu;

import edu.cegepvicto.dimhortons.ConnectionFactory;
import edu.cegepvicto.dimhortons.Menu.IngredientDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour IngredientDAO (accès BD).
 * CU002 : modification d'un item (ingrédients + extras).
 *
 * Auteur : Rachid
 */
class IngredientDAOTest {

    private IngredientDAO ingredientDAO;

    @BeforeEach
    void setUp() {
        ingredientDAO = new IngredientDAO();
    }

    /**
     * Skip les tests si MySQL n'est pas démarré
     * Astuce proposer par IA ChatGpt pour eviter que les tests explosent si problèmr de conexion BD
     */
    private void assumeDbUp() {
        try (Connection cnx = ConnectionFactory.getConnection()) {
            Assumptions.assumeTrue(cnx != null && !cnx.isClosed(), "BD indisponible : tests DAO ignorés.");
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "BD indisponible : tests DAO ignorés.");
        }
    }

    @Test
    void testFindNomsIngredientsParNomItem_cafeRegulier() {
        assumeDbUp();

        String nomItem = "Café régulier";

        List<String> ingredients = ingredientDAO.findNomsIngredientsParNomItem(nomItem);

        assertNotNull(ingredients, "La liste d'ingrédients ne doit pas être nulle");
        assertFalse(ingredients.isEmpty(), "Le café régulier doit avoir au moins un ingrédient de base");
    }

    @Test
    void testFindExtrasParNomItem_cafeRegulier() {
        assumeDbUp();

        String nomItem = "Café régulier";

        List<String> extras = ingredientDAO.findExtrasParNomItem(nomItem);

        assertNotNull(extras, "La liste d'extras ne doit pas être nulle");
        assertFalse(extras.isEmpty(), "Le café régulier doit proposer au moins un extra");
    }

    @Test
    void testFindPrixSupplement_laitDavoine_estPositif() {
        assumeDbUp();

        String nomIngredient = "Lait d'avoine";

        double prix = ingredientDAO.findPrixSupplement(nomIngredient);

        assertTrue(prix > 0.0, "Le lait d'avoine doit avoir un prix supplément > 0");
    }

    @Test
    void testFindPrixSupplement_ingredientInconnu_retourneZero() {
        assumeDbUp();

        String nomIngredient = "INGREDIENT_INEXISTANT";

        double prix = ingredientDAO.findPrixSupplement(nomIngredient);

        assertEquals(0.0, prix, 0.0001,
                "Un ingrédient inconnu doit retourner 0.0");
    }
}
