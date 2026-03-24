package edu.cegepvicto.dimhortons.Admin.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controleur pour le menu principal admin.
 * Affiche les options disponibles pour le responsable magasin.
 */
public class MenuAdminController {

    @FXML
    private Button boutonAjouterItem;

    @FXML
    private Button boutonSupprimerItem;
    @FXML
    private Button boutonCuisine;

    @FXML
    private Button boutonRetourMenuPrincipal;


    /**
     * Ouvre l'ecran d'ajout d'item quand on clique sur le bouton
     */
    @FXML
    private void ouvrirAjoutItem(ActionEvent event) {
        try {
            // charger la vue FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/cegepvicto/dimhortons/AdminView/creationitem_admin-view.fxml")
            );
            Parent root = loader.load();

            // affiche la nouvelle scene
            Stage stage = (Stage) boutonAjouterItem.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true); // garde le plein ecran

        } catch (IOException exception) {
            System.out.println("Erreur: impossible de charger la vue creation item");
        }
    }

    /*
     * Ouvre la vue cuisine
     */
    @FXML
    private void ouvrirCuisine(ActionEvent event) {
        try {
            // charger la vue FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/cegepvicto/dimhortons/cuisine/cuisine-view.fxml")
            );
            Parent root = loader.load();

            // affiche la nouvelle scene
            Stage stage = (Stage) boutonCuisine.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true); // garde le plein ecran

        } catch (IOException exception) {
            System.out.println("Erreur: impossible de charger la vue cuisine");
        }
    }

    /*
     * Retourne au menu principal de l'application
     */
    @FXML
    private void retourMenuPrincipal(ActionEvent event) {
        try {
            // charger la vue FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/cegepvicto/dimhortons/Menu/menu-view.fxml")
            );
            Parent root = loader.load();
            // affiche la nouvelle scene
            Stage stage = (Stage) boutonRetourMenuPrincipal.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true); // garde le plein ecran
        } catch (IOException exception) {
            System.out.println("Erreur: impossible de charger la vue menu principal");
        }
    }

    @FXML
    private void ouvrirSuppressionItem(ActionEvent event) {
        try {
            // Charger la vue FXML de suppression
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/edu/cegepvicto/dimhortons/AdminView/supprimer_item-view.fxml"
                    )
            );
            Parent root = loader.load();

            // Réutiliser le même stage (navigation plein écran)
            Stage stage = (Stage) boutonSupprimerItem.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);

        } catch (IOException exception) {
            System.out.println("Erreur : impossible de charger la vue suppression item");
            exception.printStackTrace();
        }
    }


}