package edu.cegepvicto.dimhortons.Paiement;

import edu.cegepvicto.dimhortons.ConnectionFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DAO pour les commandes.
 * Gère les opérations de base de données pour les commandes.
 */
public class CommandeDAO {

    /**
     * Enregistre une commande dans la base de données.
     *
     * @param commande La commande à enregistrer
     * @param utilisateurId L'ID de l'utilisateur (par défaut 1 si non connecté)
     * @return Le numéro de la commande générée
     */
    public int enregistrerCommande(Commande commande, int utilisateurId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int commandeId = 0;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            // Générer le numéro de commande unique
            String numeroCommande = genererNumeroCommande();

            // Calculer les taxes
            double prixTotal = commande.getMontantTotal();
            double taxe = prixTotal * 0.14975; // TPS 5% + TVQ 9.975%
            double montantFinal = prixTotal + taxe;

            // Insérer la commande
            String sqlCommande = "INSERT INTO Commande (numero_commande, utilisateur_id, statut, prix_total, taxe, montant_final, date_paiement) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sqlCommande, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, numeroCommande);
            pstmt.setInt(2, utilisateurId);
            pstmt.setString(3, "EN_ATTENTE");
            pstmt.setDouble(4, prixTotal);
            pstmt.setDouble(5, taxe);
            pstmt.setDouble(6, montantFinal);
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("L'insertion de la commande est ratée");
            }

            // Récupérer l'ID généré
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                commandeId = rs.getInt(1);
                commande.setNumeroCommande(commandeId);
                commande.setNumeroCommandeStr(numeroCommande);
                commande.setTaxe(taxe);
                commande.setMontantFinal(montantFinal);
            } else {
                throw new SQLException("L'insertion de la commande a échoué");
            }

            pstmt.close();

            // Insérer les lignes de commande (CommandeItem)
            String sqlLigne = "INSERT INTO CommandeItem (commande_id, item_id, quantite, prix_unitaire, prix_total) " +
                    "VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sqlLigne);

            for (LigneCommande ligne : commande.getLignes()) {
                pstmt.setInt(1, commandeId);
                pstmt.setInt(2, ligne.getItemId());
                pstmt.setInt(3, ligne.getQuantite());
                pstmt.setDouble(4, ligne.getPrixUnitaire());
                pstmt.setDouble(5, ligne.getSousTotal());
                pstmt.addBatch();
            }

            pstmt.executeBatch();

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Annuler la transaction en cas d'erreur
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return commandeId;
    }

    /**
     * Génère un numéro de commande au format CMD-YYYYMMDD-XXX
     *
     * @return Le numéro de commande généré
     */
    private String genererNumeroCommande() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = now.format(formatter);

        // Générer un nombre aleatoire simple.
        int random = (int) (Math.random() * 1000);

        return String.format("CMD-%s-%03d", date, random);
    }
}