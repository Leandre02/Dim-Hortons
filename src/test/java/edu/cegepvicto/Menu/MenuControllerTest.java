package edu.cegepvicto.Menu;

import edu.cegepvicto.dimhortons.Menu.Categorie;
import edu.cegepvicto.dimhortons.Menu.Item;
import edu.cegepvicto.dimhortons.Menu.MenuController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuControllerTest {
    private MenuController instance;
    private static Categorie categorieTest;

    @BeforeEach
    public void initialize() throws Exception {
        categorieTest = new Categorie(1, "Boissons", "Boissons chaudes");
        instance = new MenuController();

        Field instanceField = MenuController.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, instance);

        Field panierField = MenuController.class.getDeclaredField("panier");
        panierField.setAccessible(true);
        List<Item> panier = (List<Item>) panierField.get(instance);
        panier.clear();
    }

    @Test
    public void testAjouterUnItemAuPanier() throws Exception {
        Item item = new Item(1, "Café", 2.99, "/images/cafe.png", "Café chaud", categorieTest);

        MenuController.ajouterItemAuPanier(item);

        Field panierField = MenuController.class.getDeclaredField("panier");
        panierField.setAccessible(true);
        List<Item> panier = (List<Item>) panierField.get(instance);

        assertNotNull(panier, "Le panier ne doit pas être null");
        assertEquals(1, panier.size(), "Le panier doit contenir 1 item");
        assertEquals(item, panier.get(0), "L'item ajouté doit être le bon");
    }

    @Test
    public void testAjouterPlusieursItemsAuPanier() throws Exception {
        Item item1 = new Item(1, "Café", 2.99, "/images/cafe.png", "Café chaud", categorieTest);
        Item item2 = new Item(2, "Thé", 2.49, "/images/the.png", "Thé chaud", categorieTest);
        Item item3 = new Item(3, "Jus", 3.49, "/images/jus.png", "Jus d'orange", categorieTest);

        MenuController.ajouterItemAuPanier(item1);
        MenuController.ajouterItemAuPanier(item2);
        MenuController.ajouterItemAuPanier(item3);

        Field panierField = MenuController.class.getDeclaredField("panier");
        panierField.setAccessible(true);
        List<Item> panier = (List<Item>) panierField.get(instance);

        assertEquals(3, panier.size(), "Le panier doit contenir 3 items");
        assertTrue(panier.contains(item1), "Le panier doit contenir l'item 1");
        assertTrue(panier.contains(item2), "Le panier doit contenir l'item 2");
        assertTrue(panier.contains(item3), "Le panier doit contenir l'item 3");
    }

    @Test
    public void testPanierVide() throws Exception {
        Field panierField = MenuController.class.getDeclaredField("panier");
        panierField.setAccessible(true);
        List<Item> panier = (List<Item>) panierField.get(instance);

        assertEquals(0, panier.size(), "Le panier doit être vide au départ");
    }

    @Test
    public void testAjouterItemIdentique() throws Exception {
        Item item = new Item(1, "Café", 2.99, "/images/cafe.png", "Café chaud", categorieTest);

        MenuController.ajouterItemAuPanier(item);
        MenuController.ajouterItemAuPanier(item);
        MenuController.ajouterItemAuPanier(item);

        Field panierField = MenuController.class.getDeclaredField("panier");
        panierField.setAccessible(true);
        List<Item> panier = (List<Item>) panierField.get(instance);

        assertEquals(3, panier.size(), "Le panier doit contenir 3 fois le même item");
    }

    @AfterAll
    public static void cleanUp() throws Exception {
        Field instanceField = MenuController.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        MenuController instance = (MenuController) instanceField.get(null);

        if (instance != null) {
            Field panierField = MenuController.class.getDeclaredField("panier");
            panierField.setAccessible(true);
            List<Item> panier = (List<Item>) panierField.get(instance);
            panier.clear();
        }

        System.out.println("Nettoyage des tests terminé");
    }
}