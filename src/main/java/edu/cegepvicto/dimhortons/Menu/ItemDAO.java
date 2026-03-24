package edu.cegepvicto.dimhortons.Menu;

import edu.cegepvicto.dimhortons.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    // Permet de list tous les items avec catégorie
    public List<Item> findAll() throws Exception {
        try (Connection connexion = ConnectionFactory.getConnection()) {

            PreparedStatement requete = connexion.prepareStatement(
                    "SELECT i.id, i.nom, i.prix, i.image_url, i.description, " +
                            "c.id AS categorie_id, c.nom AS categorie_nom, c.description AS categorie_description " +
                            "FROM Item i LEFT JOIN CategorieItem c ON i.categorie_id = c.id " +
                            "ORDER BY i.id"
            );

            ResultSet resultats = requete.executeQuery();

            ArrayList<Item> items = new ArrayList<>();
            while (resultats.next()) {

                Categorie categorie = null;
                int categorieId = resultats.getInt("categorie_id");
                if (!resultats.wasNull()) {
                    categorie = new Categorie(
                            categorieId,
                            resultats.getString("categorie_nom"),
                            "Boissons chaudes");
                }

                Item item = new Item(
                        resultats.getInt("id"),
                        resultats.getString("nom"),
                        resultats.getDouble("prix"),
                        resultats.getString("image_url"),
                        resultats.getString("description"),
                        categorie
                );
                items.add(item);
            }

            return items;

        } catch (SQLException exception) {
            // Afficher l'erreur SQL complète
            exception.printStackTrace();
            throw new Exception("Impossible de charger les items: " + exception.getMessage());
        }
    }

    public Item findById(int id) throws Exception {
        try (Connection connexion = ConnectionFactory.getConnection()) {
            PreparedStatement requete = connexion.prepareStatement(
                    "SELECT i.id, i.nom, i.prix, i.image_url, i.description, " +
                            "c.id AS categorie_id, c.nom AS categorie_nom, c.description AS categorie_description " +
                            "FROM Item i LEFT JOIN CategorieItem c ON i.categorie_id = c.id " +
                            "WHERE i.id = ?"
            );

            requete.setInt(1, id);

            ResultSet resultats = requete.executeQuery();

            if (resultats.next()) {
                Categorie categorie = null;
                int categorieId = resultats.getInt("categorie_id");
                if (!resultats.wasNull()) {
                    categorie = new Categorie(
                            categorieId,
                            resultats.getString("categorie_nom"),
                            "Boissons chaudes");
                }

                return new Item(
                        resultats.getInt("id"),
                        resultats.getString("nom"),
                        resultats.getDouble("prix"),
                        resultats.getString("image_url"),
                        resultats.getString("description"),
                        categorie
                );
            }

            return null;

        } catch (SQLException exception) {
            // Afficher l'erreur SQL complète
            exception.printStackTrace();
            throw new Exception("Impossible de charger l'item avec l'id " + exception.getMessage());
        }
    }

    ///
    public int insert(Item item) throws Exception {
        try (Connection connexion = ConnectionFactory.getConnection()) {
            PreparedStatement requete = connexion.prepareStatement(
                    "INSERT INTO Item (nom, prix, image_url, description, categorie_id) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            requete.setString(1, item.getNom());
            requete.setDouble(2, item.getPrix());
            requete.setString(3, item.getImage());
            requete.setString(4, item.getDescription());

            if (item.getCategorie() != null) {
                requete.setInt(5, item.getCategorie().getId());
            } else {
                requete.setNull(5, Types.INTEGER);
            }

            requete.executeUpdate();

            ResultSet cleGeneree = requete.getGeneratedKeys();
            if (cleGeneree.next()) {
                int nouvelId = cleGeneree.getInt(1);
                item.setId(nouvelId);
                return nouvelId;
            } else {
                throw new Exception("Impossible de récupérer l'id généré.");
            }

        } catch (SQLException exception) {
            // Afficher l'erreur SQL complète
            exception.printStackTrace();
            throw new Exception("Impossible d'enregistrer l'objet " + exception.getMessage());
        }
    }

    public void update(Item item) throws Exception {
        try (Connection connexion = ConnectionFactory.getConnection()) {
            PreparedStatement requete = connexion.prepareStatement(
                    "UPDATE Item SET nom = ?, prix = ?, image_url = ?, description = ?, categorie_id = ? WHERE id = ?"
            );

            requete.setString(1, item.getNom());
            requete.setDouble(2, item.getPrix());
            requete.setString(3, item.getImage());
            requete.setString(4, item.getDescription());

            if (item.getCategorie() != null) {
                requete.setInt(5, item.getCategorie().getId());
            } else {
                requete.setNull(5, Types.INTEGER);
            }

            requete.setInt(6, item.getId());

            int lignesModifiees = requete.executeUpdate();

            if (lignesModifiees == 0) {
                throw new Exception("Aucune ligne modifiée pour l'id " + item.getId());
            }

        } catch (SQLException exception) {
            // Afficher l'erreur SQL complète
            exception.printStackTrace();
            throw new Exception("Impossible de mettre à jour l'objet " + item.toString() + exception.getMessage());
        }
    }

    public void delete(int item) throws Exception {
        try (Connection connexion = ConnectionFactory.getConnection()) {
            PreparedStatement requete = connexion.prepareStatement(
                    "DELETE FROM Item WHERE id = ?"
            );

            requete.setInt(1, item);
            int lignesModifiees = requete.executeUpdate();
            if (lignesModifiees == 0) {
                throw new Exception("Aucune ligne pour l'id " + item);
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new Exception("Impossible de supprimer l'objet " + item + exception.getMessage());
        }
    }
}