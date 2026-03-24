package edu.cegepvicto.dimhortons.Admin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale pour lancer l'interface admin de Dim Hortons.
 * Permet au responsable magasin de gerer les items du menu.
 */

// TODO : Ajouter l'internationalisation au projet

public class MainAdmin extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // charge le fichier FXML pour l'interface admin
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/edu/cegepvicto/dimhortons/AdminView/menu_admin-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Administration - Dim Hortons");
        stage.setMaximized(true); // plein ecran pour mieux voir
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}