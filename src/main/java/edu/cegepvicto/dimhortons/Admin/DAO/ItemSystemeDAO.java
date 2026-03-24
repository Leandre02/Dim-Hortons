package edu.cegepvicto.dimhortons.Admin.DAO;

import edu.cegepvicto.dimhortons.Admin.modeles.ItemSysteme;
import edu.cegepvicto.dimhortons.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour gerer les operations sur les items du systeme.
 * Utilise PreparedStatement pour la protection des injections SQL
 */
public class ItemSystemeDAO {

    /**
     * Cree un nouvel item dans la base de donnees.
     * Valide les champs avant insertion.
     *
     * @param item l'item a creer
     * @return l'ID genere par la BD
     */
    public int creerItemSysteme(ItemSysteme item) {
        // validation nom vide
        if (item.getNom() == null || item.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'item ne peut pas etre vide");
        }

        // validation prix negatif
        if (item.getPrix() < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas etre negatif");
        }

        // validation URL image
        if (item.getUrlImage() != null && !item.getUrlImage().isEmpty()) {
            if (!validerUrlImage(item.getUrlImage())) {
                throw new IllegalArgumentException("L'URL de l'image doit pointer vers /edu/cegepvicto/dimhortons/images/");
            }
        }

        // verification doublon
        if (itemExiste(item.getNom(), item.getCategorieId())) {
            throw new IllegalArgumentException("Un item avec ce nom existe deja dans cette categorie");
        }

        String sql = "INSERT INTO Item (nom, description, prix, calories, image_url, categorie_id, actif) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connexion = ConnectionFactory.getConnection();
             PreparedStatement requete = connexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // affectation des parametres
            requete.setString(1, item.getNom());
            requete.setString(2, item.getDescription());
            requete.setDouble(3, item.getPrix());

            // calories peut etre null
            if (item.getCalories() != null) {
                requete.setInt(4, item.getCalories());
            } else {
                requete.setNull(4, java.sql.Types.INTEGER);
            }

            requete.setString(5, item.getUrlImage());
            requete.setInt(6, item.getCategorieId());
            requete.setBoolean(7, item.isActif());

            // execute la requete preparee
            int lignesAffectees = requete.executeUpdate();

            if (lignesAffectees > 0) {
                try (ResultSet resultat = requete.getGeneratedKeys()) {
                    if (resultat.next()) {
                        int idGenere = resultat.getInt(1);
                        System.out.println("Item cree avec ID: " + idGenere + " - " + item.getNom());
                        return idGenere;
                    }
                }
            }

            System.out.println("Aucune ligne inseree");
            return 0;

        } catch (SQLException exception) {
            System.out.println("Erreur SQL lors de la creation: " + exception.getMessage());
            return 0;
        }
    }

    /**
     * Cree un nouvel item
     * @param item l'item a creer
     * @return true si creation reussie, false sinon
     */
    public boolean creerItem(ItemSysteme item) {
        try {
            return creerItemSysteme(item) > 0;
        } catch (IllegalArgumentException exception) {
            System.out.println("Validation echouee: " + exception.getMessage());
            return false;
        }
    }

    /**
     * Liste tous les items d'une categorie donnee.
     * Retourne uniquement les items actifs.
     *
     * @param categorieId l'id de la categorie
     * @return liste des items de cette categorie
     */
    public List<ItemSysteme> listerItemsParCategorie(int categorieId) {
        List<ItemSysteme> items = new ArrayList<>();

        String sql = "SELECT id, nom, description, prix, calories, image_url, categorie_id, actif FROM Item WHERE categorie_id = ? AND actif = true ORDER BY nom";

        try (Connection connexion = ConnectionFactory.getConnection();
             PreparedStatement requete = connexion.prepareStatement(sql)) {

            requete.setInt(1, categorieId);

            System.out.println("Chargement items pour categorie ID: " + categorieId);

            try (ResultSet resultat = requete.executeQuery()) {
                while (resultat.next()) {
                    ItemSysteme item = new ItemSysteme();
                    item.setId(resultat.getInt("id"));
                    item.setNom(resultat.getString("nom"));
                    item.setDescription(resultat.getString("description"));
                    item.setPrix(resultat.getDouble("prix"));

                    // gestion de la valeur nullable pour les calories
                    int calories = resultat.getInt("calories");
                    if (!resultat.wasNull()) {
                        item.setCalories(calories);
                    }

                    item.setUrlImage(resultat.getString("image_url"));
                    item.setCategorieId(resultat.getInt("categorie_id"));
                    item.setActif(resultat.getBoolean("actif"));

                    items.add(item);
                }
            }

            System.out.println(items.size() + " item(s) charge(s)");

        } catch (SQLException exception) {
            System.out.println("Erreur lors du chargement des items: " + exception.getMessage());
        }

        return items;
    }

    /**
     * Verifie si un item avec le meme nom existe deja dans la categorie.
     *
     * @param nom le nom de l'item
     * @param categorieId l'id de la categorie
     * @return true si un item avec ce nom existe deja
     */
    public boolean itemExiste(String nom, int categorieId) {
        String sql = "SELECT COUNT(*) FROM Item WHERE nom = ? AND categorie_id = ?";

        try (Connection connexion = ConnectionFactory.getConnection();
             PreparedStatement requete = connexion.prepareStatement(sql)) {

            requete.setString(1, nom);
            requete.setInt(2, categorieId);

            try (ResultSet resultat = requete.executeQuery()) {
                if (resultat.next()) {
                    return resultat.getInt(1) > 0;
                }
            }

        } catch (SQLException exception) {
            System.out.println("Erreur verification doublon: " + exception.getMessage());
        }

        return false;
    }

    /**
     * Valide que l'URL de l'image pointe vers le bon dossier ressources.
     *
     * @param urlImage l'URL a valider
     * @return true si l'URL est valide
     */
    private boolean validerUrlImage(String urlImage) {
        if (!urlImage.startsWith("/edu/cegepvicto/dimhortons/images/")) {
            return false;
        }

        // Validation de l'extension de l'image
        String urlLower = urlImage.toLowerCase();
        if (!urlLower.endsWith(".png") && !urlLower.endsWith(".jpg") && !urlLower.endsWith(".jpeg")) {
            return false;
        }
        return true;
    }

    // TODO: ajouter methode pour modifier un item existant
    // TODO: ajouter methode pour supprimer un item
}