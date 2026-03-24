package edu.cegepvicto.dimhortons.cuisine.DAO;

import edu.cegepvicto.dimhortons.ConnectionFactory;
import edu.cegepvicto.dimhortons.Internationalisation;
import edu.cegepvicto.dimhortons.cuisine.modeles.CommandeCuisine;
import edu.cegepvicto.dimhortons.cuisine.modeles.CommandeCuisine.EtatCommande;
import edu.cegepvicto.dimhortons.cuisine.modeles.CommandeCuisine.LigneCommande;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO pour gerer les operations sur les commandes cote cuisine.
 * Permet de lire les commandes actives, mettre a jour leur etat et les annuler.
 */
public class CommandeDao {

    /**
     * Lit toutes les commandes actives depuis la base de donnees.
     * @return liste des commandes en attente, en preparation ou pretes
     */
    public List<CommandeCuisine> lireCommandesActives() {
        List<CommandeCuisine> liste = new ArrayList<>();

        String texteSql = "SELECT c.id, c.numero_commande, c.statut, c.date_commande, u.prenom, u.nom FROM Commande c INNER JOIN Utilisateur u ON c.utilisateur_id = u.id WHERE c.statut IN ('EN_ATTENTE','EN_PREPARATION','PRETE') ORDER BY c.date_commande DESC";

        try (Connection connexion = ConnectionFactory.getConnection();
             PreparedStatement requete = connexion.prepareStatement(texteSql);
             ResultSet resultat = requete.executeQuery()) {

            while (resultat.next()) {
                int idCommande = resultat.getInt("id");
                String numeroCommande = resultat.getString("numero_commande");
                String statutTexte = resultat.getString("statut");
                Timestamp timestampCommande = resultat.getTimestamp("date_commande");

                // convertit le timestamp en LocalDateTime
                LocalDateTime dateCommande;
                if (timestampCommande != null) {
                    dateCommande = timestampCommande.toLocalDateTime();
                } else {
                    dateCommande = LocalDateTime.now();
                }

                String prenom = resultat.getString("prenom");
                String nom = resultat.getString("nom");
                String nomClient = prenom + " " + nom;

                // convertit le statut texte en EtatCommande
                EtatCommande etatCommande = convertirStatutBd(statutTexte);

                // cree l'objet CommandeCuisine
                CommandeCuisine commande = new CommandeCuisine(
                        idCommande,
                        nomClient,
                        numeroCommande,
                        etatCommande,
                        dateCommande
                );

                // charge les lignes de la commande
                chargerLignesCommande(connexion, commande);
                liste.add(commande);
            }

        } catch (SQLException exception) {
            System.out.println(Internationalisation.texte("erreur.dao.lecture_commandes") + " : " + exception.getMessage());
        }

        return liste;
    }

    /**
     * Charge les items commandes depuis la base de donnees pour une commande donnee.
     */
    private void chargerLignesCommande(Connection connexion, CommandeCuisine commande) throws SQLException {
        String texteSql = "SELECT ci.quantite, ci.remarques, i.nom FROM CommandeItem ci JOIN Item i ON ci.item_id = i.id WHERE ci.commande_id = ?";

        try (PreparedStatement requete = connexion.prepareStatement(texteSql)) {
            requete.setInt(1, commande.getIdCommande());

            try (ResultSet resultat = requete.executeQuery()) {
                while (resultat.next()) {
                    int quantite = resultat.getInt("quantite");
                    String nomItem = resultat.getString("nom");
                    String note = resultat.getString("remarques");

                    commande.getListeLignes().add(new LigneCommande(nomItem, quantite, note));
                }
            }
        }
    }

    /**
     * Met a jour l'etat d'une commande en validant la transition.
     */
    public void mettreEtatCommande(int idCommande, EtatCommande etatActuelAttendu, EtatCommande etatSuivant) {
        // verifie que la transition est valide
        if (!estTransitionValide(etatActuelAttendu, etatSuivant)) {
            System.out.println(Internationalisation.texte("erreur.dao.transition_invalide"));
            return;
        }

        String statutBdActuel = convertirEtatVersTexteBd(etatActuelAttendu);
        String statutBdSuivant = convertirEtatVersTexteBd(etatSuivant);

        String texteSql = "UPDATE Commande SET statut = ? WHERE id = ? AND statut = ?";

        try (Connection connexion = ConnectionFactory.getConnection();
             PreparedStatement requete = connexion.prepareStatement(texteSql)) {

            requete.setString(1, statutBdSuivant);
            requete.setInt(2, idCommande);
            requete.setString(3, statutBdActuel);
            requete.executeUpdate();

        } catch (SQLException exception) {
            System.out.println(Internationalisation.texte("erreur.dao.mise_a_jour_statut") + " : " + exception.getMessage());
        }
    }

    /**
     * Annule une commande en mettant a jour son statut en ANNULEE.
     */
    public void annulerCommande(int idCommande) {
        String texteSql = "UPDATE Commande SET statut = 'ANNULEE' WHERE id = ?";

        try (Connection connexion = ConnectionFactory.getConnection();
             PreparedStatement requete = connexion.prepareStatement(texteSql)) {

            requete.setInt(1, idCommande);
            requete.executeUpdate();

        } catch (SQLException exception) {
            System.out.println(Internationalisation.texte("erreur.dao.annulation_commande") + " : " + exception.getMessage());
        }
    }

    /**
     * Stock simplifie affiche cote cuisine.
     * TODO: Remplacer par une vraie lecture en base de donnees
     */
    public Map<String, Integer> lireStockCuisine() {
        Map<String, Integer> stock = new LinkedHashMap<>();

        stock.put("stock.cafe_regulier", 25);
        stock.put("stock.beigne_chocolat", 10);
        stock.put("stock.sandwich_poulet", 15);
        stock.put("stock.cafe_latte", 20);
        stock.put("stock.beigne_nature", 5);

        return stock;
    }

    /**
     * Verifie si la transition d'etat est valide.
     */
    private boolean estTransitionValide(EtatCommande etatActuel, EtatCommande etatSuivant) {
        if (etatActuel == EtatCommande.EN_ATTENTE && etatSuivant == EtatCommande.EN_PREPARATION) {
            return true;
        }

        if (etatActuel == EtatCommande.EN_PREPARATION && etatSuivant == EtatCommande.PRETE) {
            return true;
        }

        return false;
    }

    /**
     * Convertit le statut texte de la base de donnees en EtatCommande.
     */
    private EtatCommande convertirStatutBd(String statutTexte) {
        return switch (statutTexte) {
            case "EN_PREPARATION" -> EtatCommande.EN_PREPARATION;
            case "PRETE" -> EtatCommande.PRETE;
            default -> EtatCommande.EN_ATTENTE;
        };
    }

    /**
     * Convertit un EtatCommande en son equivalent texte pour la base de donnees.
     */
    private String convertirEtatVersTexteBd(EtatCommande etatCommande) {
        return switch (etatCommande) {
            case EN_ATTENTE -> "EN_ATTENTE";
            case EN_PREPARATION -> "EN_PREPARATION";
            case PRETE -> "PRETE";
        };
    }
}