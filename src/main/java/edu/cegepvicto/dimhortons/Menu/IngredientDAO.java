package edu.cegepvicto.dimhortons.Menu;

import edu.cegepvicto.dimhortons.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Auteur : Rachid
 * DAO pour récupérer les ingrédients d'un item à partir de la BD.
 */
public class IngredientDAO {

    private static final String SQL_INGREDIENTS_PAR_NOM_ITEM = """
            SELECT i.nom
            FROM Ingredient i
                JOIN IngredientItem ii ON ii.ingredient_id = i.id
                JOIN Item it ON it.id = ii.item_id
            WHERE it.nom = ?
            ORDER BY i.nom
            """;
    /**
     * Retourne la liste des noms d'ingrédients pour un item donné (par son nom).
     */
    public List<String> findNomsIngredientsParNomItem(String nomItem) {
        List<String> resultat = new ArrayList<>();

        try (Connection cnx = ConnectionFactory.getConnection();
             PreparedStatement ps = cnx.prepareStatement(SQL_INGREDIENTS_PAR_NOM_ITEM)) {

            ps.setString(1, nomItem);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultat.add(rs.getString("nom"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // pour debug
        }

        return resultat;
    }

    private static final String SQL_EXTRAS_PAR_NOM_ITEM = """
            SELECT i.nom
            FROM Ingredient i
                JOIN CategorieItem c ON i.categorie = c.nom
                JOIN Item it ON it.categorie_id = c.id
            WHERE it.nom = ?
              AND i.prix_supplement > 0
              AND i.actif = 1
            ORDER BY i.nom
            """;

    /**
     * Retourne la liste extras possibles en fonction de la catégorie de l’item
     *
     * @param nomItem
     * @return extras de l'item
     */
    public List<String> findExtrasParNomItem(String nomItem) {
        List<String> resultat = new ArrayList<>();

        try (Connection cnx = ConnectionFactory.getConnection();
             PreparedStatement ps = cnx.prepareStatement(SQL_EXTRAS_PAR_NOM_ITEM)) {

            ps.setString(1, nomItem);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultat.add(rs.getString("nom"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultat;
    }

    /**
     * Retourne le prix des suppléments
     *
     * @param nomIngredient
     * @return un prix
     */
    public double findPrixSupplement(String nomIngredient) {

        String sql = "SELECT prix_supplement FROM Ingredient WHERE nom = ?";

        try (Connection cnx = ConnectionFactory.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setString(1, nomIngredient);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("prix_supplement");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

}
