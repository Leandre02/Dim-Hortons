package edu.cegepvicto.dimhortons.Menu;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node; // <== important pour la boucle for sur les enfants

import java.util.ArrayList;
import java.util.List;

public class ModifierItemController {

    @FXML
    private Label labelTitre;

    @FXML
    private VBox vboxIngredients;

    @FXML
    private VBox vboxExtras;

    private Stage dialogStage;
    private Item item;

    private final IngredientDAO ingredientDAO = new IngredientDAO();

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setItem(Item item) {
        this.item = item;

        if (item == null) {
            labelTitre.setText("Aucun item sélectionné");
            vboxIngredients.getChildren().clear();
            vboxExtras.getChildren().clear();
            return;
        }

        labelTitre.setText("Modifier les ingrédients de : " + item.getNom());

        vboxIngredients.getChildren().clear();
        vboxExtras.getChildren().clear();

        // =========================
        // Ingrédients de base
        // =========================
        List<String> ingredientsActuels = ingredientDAO.findNomsIngredientsParNomItem(item.getNom());
        item.setIngredients(ingredientsActuels);

        for (String ingr : ingredientsActuels) {
            CheckBox cb = new CheckBox(ingr);
            cb.setSelected(true); // par défaut, l’ingrédient est gardé
            vboxIngredients.getChildren().add(cb);
        }

        // =========================
        // Extras possibles (par catégorie)
        // =========================
        List<String> extrasPossibles = ingredientDAO.findExtrasParNomItem(item.getNom());

        for (String extra : extrasPossibles) {

            // Prix du supplément pour cet extra (BD)
            double prixExtra = ingredientDAO.findPrixSupplement(extra);

            // Libellé affiché à l’utilisateur
            String label = extra + " (+" + String.format("%.2f", prixExtra) + "$)";

            CheckBox cb = new CheckBox(label);

            // Si l’item avait déjà cet extra, on coche
            if (item.getExtras() != null && item.getExtras().contains(extra)) {
                cb.setSelected(true);
            }

            vboxExtras.getChildren().add(cb);
        }
    }

    @FXML
    private void handleEnregistrer() {
        if (item == null) {
            fermer();
            return;
        }

        List<String> nouveauxIngredients = new ArrayList<>();
        List<String> nouveauxExtras = new ArrayList<>();

        // =========================
        // 1) Récupérer les ingrédients cochés
        // =========================
        int nbIngredientsCoches = 0;

        for (Node node : vboxIngredients.getChildren()) {
            if (node instanceof CheckBox cb) {
                if (cb.isSelected()) {
                    nbIngredientsCoches++;
                    nouveauxIngredients.add(cb.getText());
                }
            }
        }

        // Si aucun ingrédient n'est sélectionné → alerte
        if (nbIngredientsCoches == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun ingrédient sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Vous devez garder au moins un ingrédient pour cet item.");
            alert.showAndWait();
            return;
        }

        // =========================
        // 2) Récupérer les extras cochés
        // =========================
        for (Node node : vboxExtras.getChildren()) {
            if (node instanceof CheckBox cb && cb.isSelected()) {
                String texte = cb.getText();               // "Lait d’avoine (+0.50$)"
                String nomExtra = texte.split(" \\(")[0];  // "Lait d’avoine"
                nouveauxExtras.add(nomExtra);
            }
        }

        // Mise à jour de l'item
        item.setIngredients(nouveauxIngredients);
        item.setExtras(nouveauxExtras);

        // =========================
        // 3) Recalcul du prix
        // =========================
        double prixBase = item.getPrix();
        double prixExtras = 0.0;

        for (String extra : nouveauxExtras) {
            prixExtras += ingredientDAO.findPrixSupplement(extra);
        }

        item.setPrixFinal(prixBase + prixExtras);

        fermer();
    }

    @FXML
    private void handleAnnuler() {
        fermer();
    }

    private void fermer() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}
