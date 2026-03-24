package edu.cegepvicto.dimhortons.Admin.controllers;

import edu.cegepvicto.dimhortons.Menu.Item;
import edu.cegepvicto.dimhortons.Menu.ItemDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class SupprimerItemController {

    @FXML
    private ListView<Item> listViewItems;

    @FXML
    private Label labelMessage;

    private final ItemDAO itemDAO = new ItemDAO();
    private ObservableList<Item> itemsObs;

    @FXML
    private void initialize() {
        chargerItems();

        // affichage lisible dans la ListView
        listViewItems.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // affiche nom + prix
                    setText(item.getNom() + " - " + String.format("%.2f $", item.getPrix()));
                }
            }
        });

        labelMessage.setText("");
    }

    private void chargerItems() {
        try {
            List<Item> items = itemDAO.findAll();
            itemsObs = FXCollections.observableArrayList(items);
            listViewItems.setItems(itemsObs);
        } catch (Exception e) {
            labelMessage.setText("Erreur lors du chargement des items.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSupprimer() {
        Item selection = listViewItems.getSelectionModel().getSelectedItem();

        if (selection == null) {
            labelMessage.setText("Veuillez sélectionner un item à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer la suppression");
        confirm.setHeaderText("Supprimer : " + selection.getNom());
        confirm.setContentText("Cette action est irréversible.");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            itemDAO.delete(selection.getId());
            itemsObs.remove(selection);
            labelMessage.setText("Item supprimé avec succès.");
        } catch (Exception e) {
            // message simple (projet école)
            labelMessage.setText("Suppression impossible : item lié à des données existantes (ex. commandes).");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            // charge le menu admin
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/cegepvicto/dimhortons/AdminView/menu_admin-view.fxml")
            );
            Parent root = loader.load();

            // change la scene
            Stage stage = (Stage) listViewItems.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true); // garde le plein ecran

        } catch (IOException exception) {
            System.err.println("Erreur: impossible de charger le menu admin");
            exception.printStackTrace();
        }
    }
}
