package edu.cegepvicto.dimhortons.Menu;

import edu.cegepvicto.dimhortons.Paiement.PaiementController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controleur pour la vue du menu de l'application DimHortons.
 * Gere l'affichage des items du menu et l'ajout d'items au panier.
 */
public class MenuController {

    @FXML
    private VBox listeMenu;
    @FXML
    private Label compteurPanier;
    @FXML
    private Button boutonVoirPanier;
    @FXML
    private Label labelNom;
    @FXML
    private Button boutonConnexion;
    @FXML
    private Button boutonAdmin;
    @FXML
    private HBox barreCategorie;
    @FXML
    private Button payer;


    // Liste contenant les items ajoutes au panier
    private final List<Item> panier = new ArrayList<>();

    // Liste contenant tous les items disponibles au menu
    private List<Item> items = new ArrayList<>();

    private final CategorieManager categorieManager = new CategorieManager();

    // Instance de MenuController
    private static MenuController instance;

    // Map qui permet de s'occuper de la gestion des labels pour la quantité
    private static final Map<Integer, Label> labelsQuantite = new HashMap<>();

    private static final String STYLE_ACTIF = "categorie-active";

    /**
     * Accesseur de l'instance MenuController
     *
     * @return instance de menu
     */
    public static MenuController get() {
        return instance;
    }

    /**
     * Methode d'initialisation appelee automatiquement apres le chargement du FXML.
     * Configure le menu en chargeant et affichant les items disponibles.
     */
    @FXML
    private void initialize() {
        instance = this;
        chargerItems();
        configurerBoutonsCategories();
        afficherItems();
        mettreAJourCompteurPanier();

        if (boutonAdmin != null) {
            boutonAdmin.setVisible(false);
            boutonAdmin.setManaged(false);
        }

        if (boutonVoirPanier != null) {
            boutonVoirPanier.setOnAction(e -> ouvrirFenetrePanier());
        }
    }

    /**
     * Charge les items du menu depuis la base de données
     */
    private void chargerItems(){
        try {
            ItemDAO dao = new ItemDAO();
            items = dao.findAll();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configure les boutons de catégories
     * Le bouton "Tous" est activé par défaut.
     */
    private void configurerBoutonsCategories() {
        if (barreCategorie == null) return;

        for (var node : barreCategorie.getChildren()) {
            if (node instanceof Button button){
                button.setOnAction(this::onCategorieClick);
                if("Tous".equals(button.getText())){
                    button.getStyleClass().add(STYLE_ACTIF);
                }
            }
        }
    }

    /**
     * Gère le clic sur un bouton de catégorie.
     * Met à jour le style (actif)
     *
     * @param e (event)
     */
    private void onCategorieClick(ActionEvent e){
        Button button = (Button) e.getSource();
        String texte = button.getText();

        barreCategorie.getChildren().forEach(node -> {
            if (node instanceof Button b) b.getStyleClass().remove(STYLE_ACTIF);
        });
        button.getStyleClass().add(STYLE_ACTIF);

        categorieManager.setCategorie("Tous".equals(texte) ? null : texte);
        afficherItems();
    }

    /**
     * Affiche les items dans le menu en fonction de la catégorie choisit
     */
    private void afficherItems(){
        listeMenu.getChildren().clear();
        labelsQuantite.clear();

        for (Item item : categorieManager.filtrer(items)) {
            listeMenu.getChildren().add(creerLigneItem(item));
        }
    }

    /**
     * Cree la representation visuelle d'un item du menu.
     *
     * @param item L'item a afficher
     * @return HBox contenant l'image et le nom de l'item, avec un gestionnaire de clic
     */
    private HBox creerLigneItem(Item item) {
        HBox ligneItem = new HBox();
        ligneItem.getStyleClass().add("ligne-item");

        ImageView image = creerImageItem(item);

        Label nom = new Label(item.getNom());
        nom.getStyleClass().add("nom-item");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox compteur = creerCompteurQuantite(item);

        ligneItem.getChildren().addAll(image, nom, spacer, compteur);
        ligneItem.setOnMouseClicked(e -> ouvrirDetailItem(item));

        return ligneItem;
    }

    /**
     * Permet de creer le visuel de l'image
     *
     * @param item
     * @return ImageView qui contient la vue de l'image
     */
    private ImageView creerImageItem(Item item) {
        String cheminImage = item.getImage();
        ImageView image = new ImageView();
        image.setFitHeight(80);
        image.setFitWidth(80);
        image.setPreserveRatio(true);
        image.getStyleClass().add("image-item");

        if (cheminImage != null) {
            try {
                Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(cheminImage)));
                image.setImage(img);
            } catch (Exception e) {
                System.out.println("Erreur chargement image: " + cheminImage);
            }
        }
        return image;
    }

    /**
     * Crée le compteur de quantité pour un item avec les boutons + et -.
     * Permet d'ajouter ou retirer des items du panier.
     *
     * @param item
     * @return HBox contenant les boutons - et +
     */
    private HBox creerCompteurQuantite(Item item) {
        HBox conteneur = new HBox(5);
        conteneur.getStyleClass().add("conteneur-quantite");
        conteneur.setAlignment(Pos.CENTER);

        Button btnMoins = new Button("-");
        btnMoins.getStyleClass().add("bouton-quantite");

        Label labelQte = new Label(String.valueOf(compterDansPanier(item.getId())));
        labelQte.getStyleClass().add("quantite-label");
        labelsQuantite.put(item.getId(), labelQte);

        Button btnPlus = new Button("+");
        btnPlus.getStyleClass().add("bouton-quantite");

        btnMoins.setOnAction(e -> retirerDuPanier(item));
        btnPlus.setOnAction(e -> ajouterAuPanier(item));

        conteneur.getChildren().addAll(btnMoins, labelQte, btnPlus);  // <-- Cette ligne manquait!
        return conteneur;
    }

    /**
     *  Compte le nombre d'item dans le panier
     *
     * @param itemId
     * @return
     */
    private int compterDansPanier(int itemId){
        return (int) instance.panier.stream().filter(i -> i.getId() == itemId).count();
    }

    /**
     *
     * Bouton (+) qui permet d'ajouter l'item dans le panier
     * @param item
     */
    private void  ajouterAuPanier(Item item) {
        Item copie = new Item(item.getId(), item.getNom(), item.getPrix(), item.getImage(), item.getDescription(), item.getCategorie());
        copie.setPrixFinal(item.getPrix());
        panier.add(copie);
        rafraichirAffichage(item.getId());
    }

    /**
     * Bouton (-) qui permet de retirer l'item du panier
     *
     * @param item
     */
    private void retirerDuPanier(Item item) {
        for (int i = 0; i < panier.size(); i++) {
            if (panier.get(i).getId() == item.getId()) {
                panier.remove(i);
                break;
            }
        }
        rafraichirAffichage(item.getId());
    }

    /**
     * Permet de rafraichir les lables du menu (mise à jour)
     *
     * @param itemId
     */
    public void rafraichirAffichage(int itemId) {
        Label label = labelsQuantite.get(itemId);
        if (label != null) {
            label.setText(String.valueOf(instance.compterDansPanier(itemId)));
        }
        instance.mettreAJourCompteurPanier();
    }

    /**
     * Ouvre la fenetre de detail pour un item
     * @param item L'item dont on veut voir les details
     */
    private void ouvrirDetailItem(Item item) {
        try {
            URL fxmlUrl = null;

            fxmlUrl = getClass().getResource("detail-item-view.fxml");

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            DetailItemController controller = loader.getController();
            controller.initialiserItem(item);

            // Nouvelle fenêtre
            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setTitle("Detail - " + item.getNom());
            stage.setScene(new Scene(root, 550, 600));
            //Ajuste la hauteur
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setHeight(screenBounds.getHeight());
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture de la vue de detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Assigne l'utilisateur qui se connecte au variable
     * @param nomUtilisateur Le nom de l'utilisateur qui se connecte
     * @param estAdmin Si l'utilisateur est admin ou non
     */
    public void setUtilisateurConnecte(String nomUtilisateur, boolean estAdmin) {
        labelNom.setText("Bonjour " + nomUtilisateur);
        boutonConnexion.setVisible(false);

        if (estAdmin) {
            if (boutonAdmin != null) {
                boutonAdmin.setVisible(true);
                boutonAdmin.setManaged(true);
            }
        }
    }

    /**
     *  Ouvre la page admin
     */
    @FXML
    private void onBoutonAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/cegepvicto/dimhortons/AdminView/menu_admin-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) boutonConnexion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBoutonPaiement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/cegepvicto/dimhortons/Paiement/Paiement-view.fxml"));
            Parent root = loader.load();

            PaiementController controller = loader.getController();
            controller.setPanier(panier);

            Stage stage = (Stage) payer.getScene().getWindow();

            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();

            Scene scene = new Scene(root, currentWidth, currentHeight);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  Ouvre la page de connexion
     */
    @FXML
    private void onBoutonConnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/cegepvicto/dimhortons/Authentification/authentification-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) boutonConnexion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Methode statique pour ajouter un item au panier depuis n'importe ou
     * @param item L'item a ajouter au panier
     */
    public static void ajouterItemAuPanier(Item item) {
        instance.panier.add(item);
        instance.mettreAJourCompteurPanier();
    }

    /**
     * Met a jour le compteur d'articles affiche dans l'interface.
     * Le compteur affiche le nombre total d'items dans le panier.
     */
    private void mettreAJourCompteurPanier() {
        if (compteurPanier != null) {
            compteurPanier.setText(String.valueOf(panier.size()));
        }
    }

    /**
     * Auteur : Rachid
     * Méthode permettant d'ouvrir la nouvelle fenêtre du panier (panier.fxml).
     * Cette version remplace complètement l'ancien PanierDialogue.
     * Rôle :
     *   - Charger le fichier FXML du panier
     *   - Injecter la liste statique "panier" dans PanierController
     *   - Afficher une nouvelle fenêtre JavaFX modale
     */
    private void ouvrirFenetrePanier() {
        try {
            URL fxmlUrl = getClass().getResource("/edu/cegepvicto/dimhortons/Menu/panier.fxml");

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            PanierController controller = loader.getController();
            controller.setPanier(panier);

            // Nouvelle fenêtre
            // utilsiation WINDOW_MODAL pour bloquer la fenetre proncipale
            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setTitle("Votre panier");
            stage.initOwner(boutonVoirPanier.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root, 550, 600));

            // Ajuste la hauteur
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setHeight(screenBounds.getHeight());

            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture de la vue du panier: " + e.getMessage());
            e.printStackTrace();

            // Afficher une alerte à l'utilisateur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir la vue");
            alert.setContentText("Une erreur s'est produite lors de l'ouverture de la vue du panier.\nConsultez la console pour plus de détails.");
            alert.showAndWait();
        }
    }
}