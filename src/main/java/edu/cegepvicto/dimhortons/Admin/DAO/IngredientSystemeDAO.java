package edu.cegepvicto.dimhortons.Admin.DAO;

import edu.cegepvicto.dimhortons.Admin.modeles.Ingredient;
import edu.cegepvicto.dimhortons.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour gerer les operations CRUD sur les ingredients dans la base de donnees.
 */
public class IngredientSystemeDAO {

    /* Une methode pour recuperer tous les ingredients
    * @return liste des ingredients actifs
     */
    public List<Ingredient> listerIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM Ingredient WHERE actif = 1 ORDER BY nom";

        try (Connection connexion = ConnectionFactory.getConnection();
             PreparedStatement requete = connexion.prepareStatement(sql);
             ResultSet resultat = requete.executeQuery()) {

            while (resultat.next()) {
                Ingredient ingredient = new Ingredient(
                        resultat.getInt("id"),
                        resultat.getString("nom"),
                        resultat.getString("categorie"),
                        resultat.getBoolean("actif")
                );
                ingredients.add(ingredient);
            }

        } catch (SQLException exception) {
          System.out.println("Erreur lors du chargement des ingredients: " + exception.getMessage());
        }

        return ingredients;
    }

    /* Une methode pour lister les ingredients par categorie
    * @param categorie la categorie des ingredients a recuperer
    * @return liste des ingredients de la categorie donnee
     */

    public List<Ingredient> listerIngredientsParCategorie(String categorie) {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM Ingredient WHERE categorie = ? AND actif = 1 ORDER BY nom";

        try (Connection connexion = ConnectionFactory.getConnection();
             PreparedStatement requete = connexion.prepareStatement(sql)) {

            requete.setString(1, categorie);

            try (ResultSet resultat = requete.executeQuery()) {
                while (resultat.next()) {
                    Ingredient ingredient = new Ingredient(
                            resultat.getInt("id"),
                            resultat.getString("nom"),
                            resultat.getString("categorie"),
                            resultat.getBoolean("actif")
                    );
                    ingredients.add(ingredient);
                }
            }

        } catch (SQLException exception) {
            System.out.println("Erreur lors du chargement des ingredients par categorie: " + exception.getMessage());
        }

        return ingredients;
    }
}
