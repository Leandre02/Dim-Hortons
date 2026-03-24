package edu.cegepvicto.dimhortons.Admin.controllers;

import edu.cegepvicto.dimhortons.Admin.DAO.IngredientSystemeDAO;
import edu.cegepvicto.dimhortons.Admin.DAO.ItemSystemeDAO;
import edu.cegepvicto.dimhortons.Admin.modeles.CategorieItem;
import edu.cegepvicto.dimhortons.Admin.modeles.Ingredient;
import edu.cegepvicto.dimhortons.Admin.modeles.ItemSysteme;
import edu.cegepvicto.dimhortons.ConnectionFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controleur pour la creation d'items dans le systeme.
 * Permet d'ajouter des nouveaux items au menu avec validation.
 */
public class AjoutItemController {


    // colonne gauche - liste categories
    @FXML
    private ListView<CategorieItem> listeCategories;

    @FXML
    private ScrollPane scrollItems;

    @FXML
    private TilePane grilleItems;

    @FXML
    private TextField champNom;

    @FXML
    private TextArea champDescription;

    @FXML
    private TextField champPrix;

    @FXML
    private TextField champCalories;

    @FXML
    private TextField champImageUrl;

    @FXML
    private ComboBox<CategorieItem> comboCategorie;

    @FXML
    private Label labelErreur;

    @FXML
    private Label labelConfirmation;

    @FXML
    private VBox panneauFormulaire;

    @FXML
    private ListView<HBox> listeIngredients;

    @FXML
    private Button boutonRetour;


    private final ItemSystemeDAO itemDao = new ItemSystemeDAO();
    private final IngredientSystemeDAO ingredientDao = new IngredientSystemeDAO();

    // liste des ingredients disponibles
    private List<Ingredient> ingredientsDisponibles = new ArrayList<>();


    // compteur d'images chargees
    private int nombreImagesChargees = 0;

    /**
     * Initialise le controleur quand la vue est chargee
     */
    @FXML
    public void initialize() {
        System.out.println("Initialisation d'AjoutItemController");

        // on charge les categories depuis la BD
        chargerCategories();

        // Charge tous les ingredients disponibles
        chargerIngredientsDisponibles();

        // on cache le formulaire au debut
        panneauFormulaire.setVisible(false);
        panneauFormulaire.setManaged(false);

        /* lazy loading des images quand on scroll ou redimensionne
         * Début code généré par : OpenAI. (2025). ChatGPT (version 5.1 novembre 2025) [Modèle massif de
         * langage]. https://chatgpt.com
         */

        // ecoute la selection d'une categorie dans la liste de gauche
        listeCategories.getSelectionModel().selectedItemProperty().addListener(
                (obs, ancienne, nouvelle) -> {
                    if (nouvelle != null) {
                        System.out.println("Selection categorie: " + nouvelle.getNom());
                        chargerItemsCategorie(nouvelle);
                        // met a jour la combobox dans le formulaire
                        comboCategorie.getSelectionModel().select(nouvelle);

                        filtrerIngredientsParCategorie(nouvelle.getNom());
                    } else {
                        grilleItems.getChildren().clear();
                    }
                }
        );


        scrollItems.vvalueProperty().addListener((obs, oldV, newV) -> {
            chargerImagesVisibles();
        });

        scrollItems.viewportBoundsProperty().addListener((obs, oldB, newB) -> {
            chargerImagesVisibles();
        });

        // Fin code généré
    }

    /**
     * Charge toutes les categories depuis la base de donnees.
     * Met les categories dans la liste de gauche et dans la combobox.
     */
    private void chargerCategories() {
        ObservableList<CategorieItem> liste = FXCollections.observableArrayList();

        String sql = "SELECT id, nom FROM CategorieItem ORDER BY nom";

        try (Connection connexion = ConnectionFactory.getConnection();
             PreparedStatement requete = connexion.prepareStatement(sql);
             ResultSet resultats = requete.executeQuery()) {

            while (resultats.next()) {
                int id = resultats.getInt("id");
                String nom = resultats.getString("nom");
                liste.add(new CategorieItem(id, nom));
            }

            // on alimente la liste et la combobox
            listeCategories.setItems(liste);
            comboCategorie.setItems(liste);

            // on selectionne la premiere categorie par defaut
            if (!liste.isEmpty()) {
                listeCategories.getSelectionModel().selectFirst();
            } else {
                System.out.println("Aucune categorie trouvee en BD");
            }

        } catch (SQLException exception) {
            System.out.println("Erreur chargement categories: " + exception.getMessage());
            labelErreur.setText("Erreur chargement categories");
        }
    }

    /**
     * Charge les items d'une categorie dans la grille centrale.
     * Utilise le lazy loading pour charger les images
     */
    private void chargerItemsCategorie(CategorieItem categorie) {
        System.out.println("Chargement des items de la categorie: " + categorie.getNom());
        grilleItems.getChildren().clear();
        nombreImagesChargees = 0; // reset le compteur

        List<ItemSysteme> items = itemDao.listerItemsParCategorie(categorie.getId());

        if (items.isEmpty()) {
            System.out.println("Aucun item trouve pour cette categorie");
        } else {
            System.out.println(" item(s) trouve(s)");
        }

        /* on crée une carte pour chaque item
         * Source d'inspiration — modelisation du lazy laoding : https://stackoverflow.com/questions/47648617/javafx-lazy-loading-in-a-scrollpane
         * Code pour la carte inspiré de : https://coderanch.com/t/604228/java/load-ImageView-TilePane
         */
        for (ItemSysteme item : items) {
            ImageView imageView = creerImageProxy(item);

            VBox carte = new VBox(4);
            carte.getStyleClass().add("carte-item");
            carte.getChildren().add(imageView);

            Label nomLabel = new Label(item.getNom());
            nomLabel.getStyleClass().add("item-nom");

            Label prixLabel = new Label(String.format("%.2f $", item.getPrix()));
            prixLabel.getStyleClass().add("item-prix");

            carte.getChildren().addAll(nomLabel, prixLabel);
            grilleItems.getChildren().add(carte);
        }

        chargerImagesVisibles();
    }

    /**
     * Cree une ImageView "proxy" pour le lazy loading.
     */
    private ImageView creerImageProxy(ItemSysteme item) {
        String cheminImage = item.getUrlImage();
        ImageView image = new ImageView();
        image.setFitHeight(80);
        image.setFitWidth(80);
        image.setPreserveRatio(true);
        image.getStyleClass().add("image-item");

        // stocke le chemin de l'image dans le userData pour le lazy loading
        image.setUserData(cheminImage);

        image.setAccessibleText(item.getNom()); // pour les logs

        return image;
    }

    /**
     * Parcourt toutes les images et charge celles qui sont visibles a l'ecran.
     */
    private void chargerImagesVisibles() {
        for (Node node : grilleItems.getChildren()) {
            if (node instanceof VBox carte) {
                for (Node enfant : carte.getChildren()) {
                    if (enfant instanceof ImageView imageView) {
                        chargerImageSiVisible(imageView);
                    }
                }
            }
        }
    }

    /**
     * Charge l'image seulement si elle est visible dans le ScrollPane.
     * Utilise un background loader pour ne pas bloquer l'UI.
     * Source d'inspiration : https://stackoverflow.com/questions/56249776/how-to-lazy-load-images-in-javafx
     */
    private void chargerImageSiVisible(ImageView imageView) {
        if (imageView.getImage() != null) {
            return;
        }

        String cheminImage = (String) imageView.getUserData();
        if (cheminImage == null) {
            return;
        }

        String nomItem = imageView.getAccessibleText();

        // on check si l'image est visible a l'ecran
        Bounds boundsImage = imageView.localToScene(imageView.getBoundsInLocal());
        Bounds boundsViewport = scrollItems.getViewportBounds();
        Bounds boundsScroll = scrollItems.localToScene(boundsViewport);

        // si pas d'intersection, on ne charge pas
        if (!boundsScroll.intersects(boundsImage)) {
            return;
        }

        // on charge l'image en background
        try {
            String url = Objects.requireNonNull(
                    getClass().getResource(cheminImage),
                    "Image introuvable: " + cheminImage).toExternalForm();

            nombreImagesChargees++;


            Image img = new Image(url, 80, 80, true, true, true);
            imageView.setImage(img);

        } catch (Exception exception) {
            System.out.println("Erreur chargement image: " + nomItem + " (" + cheminImage + ")");
        }
    }

    /**
     * Toggle pour le formulaire d'ajout d'item quand on clique sur le bouton +
     */
    @FXML
    private void basculerFormulaire(ActionEvent event) {
        boolean estVisible = panneauFormulaire.isVisible();
        panneauFormulaire.setVisible(!estVisible);
        panneauFormulaire.setManaged(!estVisible);
    }

    /**
     * Annule la creation et vide le formulaire
     */
    @FXML
    private void annulerCreation(ActionEvent event) {
        viderFormulaire();
        labelErreur.setText("");
        labelConfirmation.setText("");
    }

    /**
     * Confirme la creation d'un nouvel item apres validation
     */
    @FXML
    private void confirmerCreation(ActionEvent event) {
        labelErreur.setText("");
        labelConfirmation.setText("");

        // on recupere les valeurs du formulaire
        String nom = champNom.getText().trim();
        String description = champDescription.getText().trim();
        String prixTexte = champPrix.getText().trim();
        String caloriesTexte = champCalories.getText().trim();
        String imageUrl = champImageUrl.getText().trim();
        CategorieItem categorie = comboCategorie.getValue();

        // validation des champs obligatoires
        if (nom.isEmpty() || prixTexte.isEmpty() || categorie == null) {
            labelErreur.setText("Nom, prix et categorie obligatoires");
            return;
        }

        // validation du prix
        double prix;
        try {
            prix = Double.parseDouble(prixTexte.replace(',', '.'));
            if (prix < 0) {
                labelErreur.setText("Prix doit etre positif");
                return;
            }
        } catch (NumberFormatException exception) {
            labelErreur.setText("Prix invalide");
            return;
        }

        // validation des calories
        Integer calories = null;
        if (!caloriesTexte.isEmpty()) {
            try {
                calories = Integer.parseInt(caloriesTexte);
                if (calories < 0) {
                    labelErreur.setText("Calories doivent etre positives");
                    return;
                }
            } catch (NumberFormatException exception) {
                labelErreur.setText("Calories invalides");
                return;
            }
        }

        // TODO: valider le format de l'URL image

        // creation de l'item
        ItemSysteme nouvelItem = new ItemSysteme();
        nouvelItem.setNom(nom);
        nouvelItem.setDescription(description);
        nouvelItem.setPrix(prix);
        nouvelItem.setCalories(calories);
        nouvelItem.setUrlImage(imageUrl);
        nouvelItem.setCategorieId(categorie.getId());
        nouvelItem.setActif(true); // par defaut actif

        // insertion en BD via le DAO
        int idItemCree = itemDao.creerItemSysteme(nouvelItem);

        if (idItemCree > 0) {
            List<Ingredient> ingredientsCoches = recupererIngredientsCoches();

            if (!ingredientsCoches.isEmpty()) {
                boolean ingredientsInseres = insererIngredientsDeBase(idItemCree, ingredientsCoches);

                if (ingredientsInseres) {
                    System.out.println(ingredientsCoches.size() + " ingredient(s) ajoute(s) a l'item");
                }
                else {
                    System.err.println("Erreur lors de l'insertion des ingredients");
                }
            }
            labelConfirmation.setText("Item ajoute avec succes!");
            viderFormulaire();

            // recharge les items de la categorie actuelle
            chargerItemsCategorie(categorie);

        } else {
            labelErreur.setText("Erreur lors de l'ajout");
        }
    }

    /**
     * Vide tous les champs du formulaire
     */
    private void viderFormulaire() {
        champNom.setText("");
        champDescription.setText("");
        champPrix.setText("");
        champCalories.setText("");
        champImageUrl.setText("");
        comboCategorie.getSelectionModel().clearSelection();
    }

    /**
     * Charge tous les ingredients disponibles depuis la base de donnees.
     * Recupere la liste des ingredients
     * Remplit la listeview avec les ingredients disponibles
     */
    private void chargerIngredientsDisponibles() {
        ingredientsDisponibles = ingredientDao.listerIngredients();

        if (ingredientsDisponibles.isEmpty()) {
            System.out.println("Aucun ingredient trouve dans la BD");
        } else {
            System.out.println(ingredientsDisponibles.size() + " ingredient(s) disponible(s)");
        }
    }

    /* Methode pour filtrer les ingredients par categorie selectionnee
     * @param categorie la categorie des ingredients a afficher
     */

    private void filtrerIngredientsParCategorie(String nomCategorie) {
        if (listeIngredients == null) {
            System.out.println("ListView listeIngredients pas encore initialisee dans le FXML");
            return;
        }
        listeIngredients.getItems().clear(); // Vide la liste avant de la remplir
        for (Ingredient ingredient : ingredientsDisponibles) {
            if (ingredient.getCategorie().equalsIgnoreCase(nomCategorie)) {
                HBox ligne = creerLigneIngredient(ingredient);
                listeIngredients.getItems().add(ligne);
            }

        }
    }

    /**
     * Cree une ligne HBox pour un ingredient dans la listeview.
     * Inclut le nom, le prix supplementaire et une checkbox pour selectionner l'ingredient.
     */
    private HBox creerLigneIngredient(Ingredient ingredient) {
        HBox ligne = new HBox(10);

        Label nomLabel = new Label(ingredient.getNom());
        nomLabel.setPrefWidth(200);


        CheckBox selectionBox = new CheckBox();
        selectionBox.setSelected(false); // par defaut non selectionne

        ligne.getChildren().addAll(nomLabel, selectionBox);
        return ligne;
    }

    /* Recupere la liste des ingredients selectionnes dans la listeview
     * @return liste des ingredients selectionnes
     */
    private List<Ingredient> recupererIngredientsCoches() {
        List<Ingredient> ingredientsCoches = new ArrayList<>();

        for (HBox ligne : listeIngredients.getItems()) {
            CheckBox checkBox = (CheckBox) ligne.getChildren().get(1);
            if (checkBox.isSelected()) {
                Label nomLabel = (Label) ligne.getChildren().get(0);
                String nomIngredient = nomLabel.getText();

                // Trouve l'ingredient correspondant dans la liste des ingredients disponibles
                for (Ingredient ingredient : ingredientsDisponibles) {
                    if (ingredient.getNom().equals(nomIngredient)) {
                        ingredientsCoches.add(ingredient);
                        break;
                    }
                }
            }
        }
        return ingredientsCoches;
    }

    /* Insere les ingredients de base pour un nouvel item cree
     * @param itemId l'id de l'item cree
     * @param ingredients la liste des ingredients a inserer
     * @return true si insertion reussie, false sinon
     */
    private boolean insererIngredientsDeBase(int itemId, List<Ingredient> ingredients) {
        String sql = "INSERT INTO IngredientItem (item_id, ingredient_id, quantite, obligatoire, unite) VALUES (?, ?, ?, ?, ?)";

        try (Connection connexion = ConnectionFactory.getConnection();
             PreparedStatement requete = connexion.prepareStatement(sql)) {

            for (Ingredient ingredient : ingredients) {
                requete.setInt(1, itemId);
                requete.setInt(2, ingredient.getId());
                requete.setDouble(3, 1.0); // quantite par defaut
                requete.setBoolean(4, false); // non obligatoire par defaut
                requete.setString(5, "unit"); // unite par defaut

                requete.addBatch();
            }

            int[] resultats = requete.executeBatch();

            for (int res : resultats) {
                if (res == PreparedStatement.EXECUTE_FAILED) {
                    System.out.println("Echec insertion d'un ingredient");
                    return false;
                }
            }

            return true;

        } catch (SQLException exception) {
            System.out.println("Erreur SQL lors de l'insertion des ingredients: " + exception.getMessage());
            return false;
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