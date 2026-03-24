package edu.cegepvicto.dimhortons.Menu;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Contrôleur pour la vue de détail d'un item.
 * Affiche les informations complètes d'un produit et permet de l'ajouter au panier.
 */
public class DetailItemController {

    @FXML private Button boutonRetour;
    @FXML private ImageView imageItem;
    @FXML private Label nomItem;
    @FXML private Label prixItem;
    @FXML private Label descriptionItem;
    @FXML private Label informationItem;
    @FXML private Button boutonMoins;
    @FXML private Label quantiteLabel;
    @FXML private Button boutonPlus;
    @FXML private Button boutonAjouter;
    @FXML private Label prixTotalLabel;

    private Item itemSelectionne;
    private int quantite = 1;
    private Stage stage;

    /**
     * Initialise la vue avec un item spécifique
     * @param item L'item à afficher
     */
    public void initialiserItem(Item item) {
        this.itemSelectionne = item;
        afficherDetailsItem();
    }

    /**
     * Définit le stage pour pouvoir le fermer
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Affiche les détails de l'item dans l'interface
     */
    private void afficherDetailsItem() {
        // Nom du produit
        nomItem.setText(itemSelectionne.getNom());

        // Prix
        prixItem.setText(String.format("%.2f $", itemSelectionne.getPrix()));

        // Description
        if (itemSelectionne.getDescription() != null && !itemSelectionne.getDescription().isEmpty()) {
            descriptionItem.setText(itemSelectionne.getDescription());
        } else {
            descriptionItem.setText("100% du produit de la vente est reversé à des organismes locaux et aux Camps Tim");
        }

        // Nutrition
        informationItem.setText(
                "Les informations sur la nutrition, les allergènes, et les ingrédients correspondent aux produits standards du menu; " +
                        "elles peuvent varier si les produits sont personnalisés ou pour les demandes spéciales.\n\n" +
                        "Les adultes et les jeunes (âgés de 13 ans et plus) ont besoin d'une moyenne de 2 000 calories par jour, " +
                        "et les enfants (âgés de 4 à 12 ans) ont besoin d'une moyenne de 1 500 calories par jour. " +
                        "Cependant, les besoins individuels peuvent varier."
        );

        // Image
        chargerImage();

        // Mettre à jour le prix total
        mettreAJourPrixTotal();
    }

    /**
     * Charge l'image de l'item
     */
    private void chargerImage() {
        String cheminImage = itemSelectionne.getImage();
        if (cheminImage != null) {
            try {
                Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(cheminImage)));
                imageItem.setImage(img);
            } catch (Exception e) {
                System.out.println("Erreur chargement image: " + cheminImage);
                e.printStackTrace();
            }
        }
    }

    /**
     * Diminue la quantité (minimum 1)
     */
    @FXML
    private void diminuerQuantite() {
        if (quantite > 1) {
            quantite--;
            quantiteLabel.setText(String.valueOf(quantite));
            mettreAJourPrixTotal();
        }
    }

    /**
     * Augmente la quantité
     */
    @FXML
    private void augmenterQuantite() {
        quantite++;
        quantiteLabel.setText(String.valueOf(quantite));
        mettreAJourPrixTotal();
    }

    /**
     * Met à jour l'affichage du prix total
     */
    private void mettreAJourPrixTotal() {
        double prixTotal = itemSelectionne.getPrix() * quantite;
        prixTotalLabel.setText(String.format("%.2f $", prixTotal));
    }

    /**
     * Ajoute l'item au panier et retourne au menu
     */
    @FXML
    private void ajouterAuPanier() {
        // Ajouter l'item au panier le nombre de fois spécifié par la quantité
        for (int i = 0; i < quantite; i++) {
            MenuController.ajouterItemAuPanier(itemSelectionne);
        }

        MenuController.get().rafraichirAffichage(itemSelectionne.getId());

        // Retourner au menu
        retourMenu();
    }

    /**
     * Retourne à la vue du menu
     */
    @FXML
    private void retourMenu() {
        if (stage != null) {
            stage.close();
        }
    }
}