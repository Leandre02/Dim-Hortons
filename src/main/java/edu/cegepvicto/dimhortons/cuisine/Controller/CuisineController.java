package edu.cegepvicto.dimhortons.cuisine.Controller;

import edu.cegepvicto.dimhortons.Internationalisation;
import edu.cegepvicto.dimhortons.cuisine.DAO.CommandeDao;
import edu.cegepvicto.dimhortons.cuisine.modeles.CommandeCuisine;
import edu.cegepvicto.dimhortons.cuisine.modeles.CommandeCuisine.EtatCommande;
import edu.cegepvicto.dimhortons.cuisine.modeles.CommandeCuisine.LigneCommande;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.stage.Stage;
import java.util.Locale;


/**
 * Contrôleur pour l'interface cuisine.
 * Affiche les commandes et permet la mise à jour de leur état.
 */
public class CuisineController {

    @FXML
    private FlowPane zoneCommandes;

    @FXML
    private GridPane grilleStock;

    @FXML
    private Button boutonFiltre;

    @FXML
    private Button boutonLangue;

    @FXML
    private VBox panneauStock;

    @FXML
    private Label labelTitreCuisine;

    @FXML
    private Label labelStock;

    @FXML
    private Button boutonRetour;

    @FXML
    private void AfficherMenu() {

        panneauStock.setVisible(!panneauStock.isVisible());
    }


    @FXML
    private void filtrerCommandes() {

        filtreAttente = !filtreAttente;
        mettreTexteFiltre();
        rafraichirCommandes();
    }

    @FXML
    public void initialize() {
        mettreTexteDynamique();
        stockCuisine = commandeDao.lireStockCuisine();

        chargerCommandes();
        afficherStock();
        rafraichirCommandes();
        mettreTexteFiltre();
        mettreTexteBoutonLangue();
    }

    // Proprietes
    private final List<CommandeCuisine> listeCommandes = new ArrayList<>();

    // Le stock actuel de la cuisine
    private Map<String, Integer> stockCuisine;

    private boolean filtreAttente = false;
    private final DateTimeFormatter formatHeure = DateTimeFormatter.ofPattern("HH:mm");

    private final CommandeDao commandeDao = new CommandeDao();

    // Une methode pour charger les commandes depuis la base de donnees
    private void chargerCommandes() {
        listeCommandes.clear();
        listeCommandes.addAll(commandeDao.lireCommandesActives());
    }

    // Une methode pour mettre a jour les textes dynamiquement
    private void mettreTexteDynamique() {
        labelTitreCuisine.setText(Internationalisation.texte("cuisine.barre.titre"));
        labelStock.setText(Internationalisation.texte("cuisine.stock.titre"));
    }

    /*  Une methode pour basculer la langue de l'interface
     */
    @FXML
    private void basculerLangue() {

        if (Internationalisation.estFrancais()) {
            Internationalisation.changerLocale(Locale.ENGLISH);
        } else {
            Internationalisation.changerLocale(Locale.FRENCH);
        }

        // Recharge l'interface avec les nouvelles traductions
        mettreTexteDynamique();
        afficherStock();
        chargerCommandes();
        rafraichirCommandes();
        mettreTexteFiltre();
        mettreTexteBoutonLangue();

        // Met à jour le titre de la fenêtre
        var stage = (Stage) boutonLangue.getScene().getWindow();
        stage.setTitle(Internationalisation.texte("cuisine.titre.application"));
    }



    // Une méthode pour afficher le stock dans la grille
    private void afficherStock() {

        grilleStock.getChildren().clear(); // Efface les éléments existants
        int ligne = 0; // Compteur de lignes

        // Ajoute chaque élément du stock à la grille
        for (Map.Entry<String, Integer> entree : stockCuisine.entrySet()) {
            String texteStock = Internationalisation.texte(entree.getKey());

            Label etiquetteNom = new Label(texteStock);
            Label etiquetteQuantite = new Label(String.valueOf(entree.getValue()));

            GridPane.setMargin(etiquetteNom, new Insets(2, 8, 2, 0));
            GridPane.setMargin(etiquetteQuantite, new Insets(2, 0, 2, 8));

            grilleStock.add(etiquetteNom, 0, ligne);
            grilleStock.add(etiquetteQuantite, 1, ligne);
            ligne++;
        }
    }

    /* Méthode pour rafraîchir l'affichage des commandes
    * Affiche les commandes dans la zone dédiée en fonction du filtre appliqué
     */
    private void rafraichirCommandes() {

        zoneCommandes.getChildren().clear();

        for (CommandeCuisine commande : listeCommandes) {

            if (filtreAttente && commande.getEtatCommande() != EtatCommande.EN_ATTENTE) {
                continue;
            }

            VBox carteCommande = new VBox(4);
            carteCommande.getStyleClass().add("carte-commande");
            carteCommande.setPadding(new Insets(8));

            String texteTitre = Internationalisation.texte("cuisine.commande.titre")
                    + " " + commande.getNomTable();

            Label etiquetteTitre = new Label(texteTitre);
            etiquetteTitre.getStyleClass().add("titre-commande");

            Label etiquetteClient = new Label(
                    Internationalisation.texte("cuisine.commande.client")
                            + " " + commande.getNomClient()
            );

            Label etiquetteHeure = new Label(
                    Internationalisation.texte("cuisine.commande.heure")
                            + " "
                            + commande.getDateCommande().format(formatHeure)
            );

            Label etiquetteEtat = new Label(texteEtat(commande.getEtatCommande()));
            etiquetteEtat.getStyleClass().add("badge-etat");
            etiquetteEtat.getStyleClass().add(classeEtat(commande.getEtatCommande()));

            VBox zoneLignes = new VBox(2);

            // Ajoute les commentaires de chaque ligne de la commande
            for (LigneCommande ligne : commande.getListeLignes()) {

                StringBuilder texteLigne = new StringBuilder();
                texteLigne.append("- ")
                        .append(ligne.getQuantite())
                        .append(" x ")
                        .append(ligne.getNomItem());

                String note = ligne.getNote();

                if (note != null && !note.isBlank()) {
                    texteLigne.append(" (").append(note).append(")");
                }

                zoneLignes.getChildren().add(new Label(texteLigne.toString()));
            }

            // Zone des boutons d'action
            HBox zoneBoutons = new HBox(6);

            if (commande.getEtatCommande() == EtatCommande.EN_ATTENTE) {

                Button boutonPreparation = new Button(
                        Internationalisation.texte("cuisine.bouton.preparer")
                );

                boutonPreparation.getStyleClass().add("bouton-principal");
                boutonPreparation.setOnAction(
                        evenement -> changerEtatCommande(
                                commande,
                                EtatCommande.EN_ATTENTE,
                                EtatCommande.EN_PREPARATION
                        )
                );

                zoneBoutons.getChildren().add(boutonPreparation);

            } else if (commande.getEtatCommande() == EtatCommande.EN_PREPARATION) {

                Button boutonPrete = new Button(
                        Internationalisation.texte("cuisine.bouton.prete")
                );

                boutonPrete.getStyleClass().add("bouton-principal");
                boutonPrete.setOnAction(
                        evenement -> changerEtatCommande(
                                commande,
                                EtatCommande.EN_PREPARATION,
                                EtatCommande.PRETE
                        )
                );

                zoneBoutons.getChildren().add(boutonPrete);
            }

            Button boutonSupprimer = new Button(
                    Internationalisation.texte("cuisine.bouton.supprimer")
            );

            boutonSupprimer.getStyleClass().add("bouton-supprimer");
            boutonSupprimer.setOnAction(
                    evenement -> supprimerCommande(commande)
            );

            zoneBoutons.getChildren().add(boutonSupprimer);

            carteCommande.getChildren().addAll(
                    etiquetteTitre,
                    etiquetteClient,
                    etiquetteHeure,
                    etiquetteEtat,
                    zoneLignes,
                    zoneBoutons
            );

            zoneCommandes.getChildren().add(carteCommande);
        }
    }

    // Une méthode pour obtenir le texte internationalisé correspondant à un état de commande
    private String texteEtat(EtatCommande etatCommande) {

        return switch (etatCommande) {
            case EN_ATTENTE -> Internationalisation.texte("cuisine.etat.en_attente");
            case EN_PREPARATION -> Internationalisation.texte("cuisine.etat.en_preparation");
            case PRETE -> Internationalisation.texte("cuisine.etat.prete");
        };
    }

    // Une méthode pour obtenir la classe CSS correspondant à un état de commande
    private String classeEtat(EtatCommande etatCommande) {

        return switch (etatCommande) {
            case EN_ATTENTE -> "badge-attente";
            case EN_PREPARATION -> "badge-prepa";
            case PRETE -> "badge-pret";
        };
    }

    // Une méthode pour changer l'état d'une commande en appelant le DAO
    private void changerEtatCommande(CommandeCuisine commande, EtatCommande etatActuelAttendu, EtatCommande etatSuivant) {

        commande.setEtatCommande(etatSuivant);

        commandeDao.mettreEtatCommande(
                commande.getIdCommande(),
                etatActuelAttendu,
                etatSuivant
        );

        chargerCommandes();
        rafraichirCommandes();
    }

    /* Une méthode pour supprimer une commande en appelant le DAO
    * Met à jour l'affichage après suppression
     */

    private void supprimerCommande(CommandeCuisine commande) {

        commandeDao.annulerCommande(commande.getIdCommande());
        chargerCommandes();
        rafraichirCommandes();
    }

    /* Une méthode pour mettre à jour le texte du bouton de filtre
     */
    private void mettreTexteFiltre() {

        if (boutonFiltre == null) {
            return;
        }

        if (filtreAttente) {
            boutonFiltre.setText(Internationalisation.texte("cuisine.bouton.filtre.toutes"));
        } else {
            boutonFiltre.setText(Internationalisation.texte("cuisine.bouton.filtre.attente"));
        }
    }

    /* Une méthode pour mettre à jour le texte du bouton de langue
     */
    private void mettreTexteBoutonLangue() {

        if (Internationalisation.estFrancais()) {
            boutonLangue.setText(Internationalisation.texte("cuisine.bouton.langue.en"));
        } else {
            boutonLangue.setText(Internationalisation.texte("cuisine.bouton.langue.fr"));
        }
    }

    /**
     * Retourne au menu admin principal
     */
    @FXML
    private void retourMenuAdmin(ActionEvent event) {
        try {
            // charge le menu admin
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/cegepvicto/dimhortons/AdminView/menu_admin-view.fxml")
            );
            Parent root = loader.load();

            // change la scene
            Stage stage = (Stage) boutonRetour.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true); // garde le plein ecran

        } catch (IOException exception) {
            System.err.println("Erreur: impossible de charger le menu admin");
            exception.printStackTrace();
        }
    }

}
