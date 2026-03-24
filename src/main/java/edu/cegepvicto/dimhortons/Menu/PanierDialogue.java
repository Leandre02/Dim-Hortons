package edu.cegepvicto.dimhortons.Menu;

import javafx.scene.control.Alert;

import java.util.List;

/**
 * Classe pour afficher le panier dans un dialogue simple.
 */
public class PanierDialogue {

    // ===========================================================
// NOTE POUR CU002 :
// Le dialogue  doit :
// 1- d’afficher la liste des items du panier,
// 2- de sélectionner un item,
// 3- d’ouvrir la fenêtre FXML (modifier_item.fxml)
//    pour modifier ses ingrédients.
//
// → À faire : remplacer la méthode afficher() par une version
//   utilisant une ListView + un bouton "Modifier".
// → À faire : ajouter une méthode ouvrirFenetreModification(Item item)
//   qui charge le FXML et appelle ModifierItemController.
// ===========================================================


    /**
     * Affiche le panier dans un dialogue TEMPORAIRE TODO
     *
     * @param panier La liste des items
     */
    public static void afficher(List<Item> panier) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mon Panier");
        alert.setHeaderText("Contenu du panier");

        if (panier.isEmpty()) {
            alert.setContentText("Votre panier est vide");
        } else {
            // Créer la liste des items
            StringBuilder contenu = new StringBuilder();
            for (Item item : panier) {
                contenu.append("- ").append(item.getNom()).append("\n");
            }
            alert.setContentText(contenu.toString());
        }

        alert.showAndWait();
    }
}
