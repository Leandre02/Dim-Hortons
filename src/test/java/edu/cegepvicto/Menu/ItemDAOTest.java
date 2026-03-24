package edu.cegepvicto.Menu;

import edu.cegepvicto.dimhortons.Menu.Categorie;
import edu.cegepvicto.dimhortons.Menu.Item;
import edu.cegepvicto.dimhortons.Menu.ItemDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ItemDAOTest {

    private ItemDAO itemDAO;
    private final List<Integer> idsToCleanup = new ArrayList<>();
    private String originalNameItem1 = null;

    @BeforeEach
    public void initialize() throws Exception {
        itemDAO = new ItemDAO();

        Item item1 = itemDAO.findById(1);
        if (item1 != null) {
            originalNameItem1 = item1.getNom();
        }
    }

    // Test cleanup : Aide ChatGPT
    @AfterEach
    public void cleanup() throws Exception {
        for (Integer id : idsToCleanup) {
            try {
                itemDAO.delete(id);
            } catch (Exception ignored) {}
        }
        idsToCleanup.clear();

        if (originalNameItem1 != null) {
            Item item1 = itemDAO.findById(1);
            if (item1 != null) {
                item1.setNom(originalNameItem1);
                itemDAO.update(item1);
            }
            originalNameItem1 = null;
        }
    }

    @Test
    public void testFindAll() throws Exception {
        List<Item> items = itemDAO.findAll();

        assertNotNull(items, "La liste ne doit pas être nulle");
        assertFalse(items.isEmpty(), "La liste doit contenir au moins un item");
    }

    @Test
    public void testInsert() throws Exception {
        Categorie categorie = new Categorie(1, "Test", "TEST");
        Item nouvelItem = new Item(0, "Item Test", 5.99, "/images/test.png", "Description test", categorie);

        int idGenere = itemDAO.insert(nouvelItem);
        idsToCleanup.add(idGenere);

        assertTrue(idGenere > 0, "L'id généré doit être positif");
        assertEquals(idGenere, nouvelItem.getId(), "L'id doit être assigné à l'item");
    }

    @Test
    public void testUpdate() throws Exception {
        Item item = itemDAO.findById(1);
        assertNotNull(item, "L'item doit exister avant la mise à jour");

        String nouveauNom = "Nom Modifié";
        item.setNom(nouveauNom);

        itemDAO.update(item);

        Item itemModifie = itemDAO.findById(1);
        assertEquals(nouveauNom, itemModifie.getNom(), "Le nom doit être mis à jour");
    }

    @Test
    public void testDelete() throws Exception {
        Categorie categorie = new Categorie(1, "Test", "TEST");
        Item itemASupprimer = new Item(0, "Item à Supprimer", 3.99, "/images/delete.png", "Test suppression", categorie);

        int idGenere = itemDAO.insert(itemASupprimer);
        assertTrue(idGenere > 0, "L'item doit être inséré avec succès");

        Item itemRecupere = itemDAO.findById(idGenere);
        assertNotNull(itemRecupere, "L'item doit exister avant la suppression");

        itemDAO.delete(idGenere);

        Item itemSupprime = itemDAO.findById(idGenere);
        assertNull(itemSupprime, "L'item ne doit plus exister après la suppression");
    }
}