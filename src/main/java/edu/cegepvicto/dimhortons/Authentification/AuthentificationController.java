package edu.cegepvicto.dimhortons.Authentification;

import edu.cegepvicto.dimhortons.ConnectionFactory;
import edu.cegepvicto.dimhortons.Menu.MenuController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Controleur pour la connexion utilisateur de DimHortons.
 * Gere la validation des identifiants et la navigation vers le menu principal.
 */
public class AuthentificationController {

    @FXML
    private Button boutonRetourMenu;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField champMotDePasse;
    @FXML
    private Label labelErreur;

    private AuthentificationService authService = new AuthentificationService();

    /**
     * Methode appelee automatiquement apres le chargement du FXML.
     */
    @FXML
    private void initialize() {
        labelErreur.setText("");
    }

    /**
     * Appelée lorsque l'utilisateur clique sur "Se connecter"
     */
    @FXML
    private void onAuthentification() {

        String emailFieldText = emailField.getText();
        String motDePasse = champMotDePasse.getText();

        if (emailFieldText.isEmpty() || motDePasse.isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs.");
            return;
        }

        AuthentificationService.ResultatConnexion resultat = authService.valider(emailFieldText, motDePasse);

        if (resultat == null) {
            afficherErreur("Email ou mot de passe incorrect.");
            return;
        }
        else
        {
            try {
                URL fxmlLocation = getClass().getResource("/edu/cegepvicto/dimhortons/Menu/menu-view.fxml");
                FXMLLoader loader = new FXMLLoader(fxmlLocation);
                Parent root = loader.load();

                MenuController menuController = loader.getController();

                menuController.setUtilisateurConnecte(resultat.nom, resultat.estAdmin);

                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
                afficherErreur("Erreur lors du chargement du menu.");
            }
        }
    }

    @FXML
    private void onRetourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/cegepvicto/dimhortons/Menu/menu-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) boutonRetourMenu.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche un message d'erreur sous le formulaire
     */
    private void afficherErreur(String message) {
        labelErreur.setText(message);
    }
}
