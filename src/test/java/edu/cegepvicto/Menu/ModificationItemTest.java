package edu.cegepvicto.Menu;

import edu.cegepvicto.dimhortons.ConnectionFactory;
import edu.cegepvicto.dimhortons.Menu.Categorie;
import edu.cegepvicto.dimhortons.Menu.IngredientDAO;
import edu.cegepvicto.dimhortons.Menu.Item;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests simples de la logique du CU002 :
 * extras sélectionnés, recalcul du prixFinal.
 *
 * Auteur : Rachid
 */
class ModificationItemTest {

    private void assumeDbUp() {
        try (Connection cnx = ConnectionFactory.getConnection()) {
            Assumptions.assumeTrue(cnx != null && !cnx.isClosed(), "BD indisponible : tests ignorés.");
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "BD indisponible : tests ignorés.");
        }
    }

    private double calculerPrixFinal(Item item, IngredientDAO ingredientDAO) {
        double prixBase = item.getPrix();

        List<String> extras = item.getExtras();
        if (extras == null || extras.isEmpty()) {
            return prixBase;
        }

        double prixExtras = 0.0;
        for (String extra : extras) {
            prixExtras += ingredientDAO.findPrixSupplement(extra);
        }

        return prixBase + prixExtras;
    }

    @Test
    void testRecalculPrixAvecUnExtra_prixAugmente() {
        assumeDbUp();

        IngredientDAO ingredientDAO = new IngredientDAO();

        Categorie catCafe = new Categorie(1, "Cafés", "Boissons chaudes");
        Item item = new Item(1, "Café régulier", 1.99, "/img/cafe.jpg", "Café noir", catCafe);

        item.setExtras(Arrays.asList("Lait d'avoine"));

        double prixFinal = calculerPrixFinal(item, ingredientDAO);
        item.setPrixFinal(prixFinal);

        assertTrue(item.getPrixFinal() > item.getPrix(),
                "Avec un extra payant, le prix final doit être supérieur au prix de base");
    }

    @Test
    void testRecalculPrixSansExtra_prixInchange() {
        assumeDbUp();

        IngredientDAO ingredientDAO = new IngredientDAO();

        Categorie catCafe = new Categorie(1, "Cafés", "Boissons chaudes");
        Item item = new Item(1, "Café régulier", 1.99, "/img/cafe.jpg", "Café noir", catCafe);

        item.setExtras(null);

        double prixFinal = calculerPrixFinal(item, ingredientDAO);
        item.setPrixFinal(prixFinal);

        assertEquals(item.getPrix(), item.getPrixFinal(), 0.0001,
                "Sans extra, le prix final doit rester égal au prix de base");
    }

    @Test
    void testRecalculPrixAvecDeuxExtras_prixAugmentePlus() {
        assumeDbUp();

        IngredientDAO ingredientDAO = new IngredientDAO();

        Categorie catCafe = new Categorie(1, "Cafés", "Boissons chaudes");
        Item item = new Item(1, "Café régulier", 1.99, "/img/cafe.jpg", "Café noir", catCafe);

        item.setExtras(Arrays.asList("Lait d'avoine", "Sirop vanille"));

        double prixFinal = calculerPrixFinal(item, ingredientDAO);
        item.setPrixFinal(prixFinal);

        assertTrue(item.getPrixFinal() > item.getPrix(),
                "Avec 2 extras, le prix final doit être supérieur au prix de base");
    }
}
