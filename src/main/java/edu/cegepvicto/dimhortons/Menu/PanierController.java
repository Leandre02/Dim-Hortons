package edu.cegepvicto.dimhortons.Menu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Contrôleur de la fenêtre panier (ListView).
 * Permet de visualiser les items du panier et de modifier l'item sélectionné (CU002).
 * Debug fait à l'aide de ChatGpt
 */
public class PanierController {

    @FXML
    private ListView<Item> listViewPanier;

    private ObservableList<Item> itemsPanier;


    private Stage stage;


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Injecté par MenuController pour initialiser la liste du panier.
     */
    public void setPanier(List<Item> panier) {
        this.itemsPanier = FXCollections.observableArrayList(panier);
        listViewPanier.setItems(itemsPanier);

        // Affichage simple : "Nom - 3.49 $ - nb ingredients -  nb Extras "
        listViewPanier.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    try {
                        String nom = item.getNom();
                        double prix = item.getPrixFinal();
                        System.out.println("DEBUG CELL: nom=" + nom + ", prix=" + prix);

                        String texte = nom + " - " + String.format("%.2f $", prix);

                        // Ajout des ingrédients et extras
                        int nbIng = (item.getIngredients() != null) ? item.getIngredients().size() : 0;
                        int nbExtras = (item.getExtras() != null) ? item.getExtras().size() : 0;

                        if (nbIng > 0) {
                            texte += " (" + nbIng + " ingr.)";
                        }
                        if (nbExtras > 0) {
                            texte += " + " + nbExtras + " extra" + (nbExtras > 1 ? "s" : "");
                        }

                        setText(texte);
                    } catch (Exception e) {
                        System.out.println("ERREUR dans cellFactory: " + e.getMessage());
                        setText(item.getNom() + " (erreur prix)");
                    }
                }
            }
        });

    }

    @FXML
    private void handleModifier() {
        Item selection = listViewPanier.getSelectionModel().getSelectedItem();

        if (selection == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun item sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un item dans le panier avant de le modifier.");
            alert.showAndWait();
            return;
        }

        ouvrirFenetreModification(selection);
        listViewPanier.refresh();
    }

    @FXML
    private void handleFermer() {
        // stage.close();
        Stage stage = (Stage) listViewPanier.getScene().getWindow();
        stage.close();
    }

    private void ouvrirFenetreModification(Item item) {
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("modifier_item.fxml")
            );

            if (loader.getLocation() == null) {
                System.out.println("DEBUG: URL de modifier_item.fxml est NULL");
            }

            Parent root = loader.load();

            ModifierItemController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modifier l'item : " + item.getNom());
            dialogStage.initOwner(listViewPanier.getScene().getWindow());
            dialogStage.initModality(Modality.WINDOW_MODAL);

            controller.setDialogStage(dialogStage);
            controller.setItem(item);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/edu/cegepvicto/dimhortons/styles.css").toExternalForm()
            );
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace(); // afficher d ou vient l erreur
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Une erreur est survenue lors de l'ouverture de la fenêtre de modification.");
            alert.showAndWait();
        }
    }
    // Traitement de paiement
    @FXML
    private void handlePayer() {

    }
}