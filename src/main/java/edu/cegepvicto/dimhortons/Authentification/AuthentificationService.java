package edu.cegepvicto.dimhortons.Authentification;

import edu.cegepvicto.dimhortons.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Service responsable de la validation des identifiants.
 * Retourne le nom de la personne si connexion valide
 */
public class AuthentificationService {

    public static class ResultatConnexion {
        public String nom;
        public boolean estAdmin;

        public ResultatConnexion(String nom, boolean estAdmin) {
            this.nom = nom;
            this.estAdmin = estAdmin;
        }
    }

    public ResultatConnexion valider(String email, String password) {

        String sql = "SELECT prenom, admin FROM Utilisateur WHERE email = ? AND mot_de_passe_hash = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {

            prep.setString(1, email);
            prep.setString(2, password);

            ResultSet rs = prep.executeQuery();

            if (rs.next()) {
                String nom = rs.getString("prenom");
                boolean estAdmin = rs.getBoolean("admin");
                return new ResultatConnexion(nom, estAdmin);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Erreur Authentification: " + e.getMessage());
            return null;
        }
    }
}
