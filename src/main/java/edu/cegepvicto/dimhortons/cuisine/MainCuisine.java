package edu.cegepvicto.dimhortons.cuisine;

import edu.cegepvicto.dimhortons.Internationalisation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Point d'entrée JavaFX pour l'interface cuisine.
 */
public class MainCuisine extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader chargeur = new FXMLLoader(
                MainCuisine.class.getResource("cuisine-view.fxml")
        );

        Scene scene = new Scene(chargeur.load());

        stage.setTitle(Internationalisation.texte("cuisine.titre.application"));
        stage.setScene(scene);
        stage.setMaximized(true); // Plein écran
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
