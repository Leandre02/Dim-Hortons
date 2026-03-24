package edu.cegepvicto.dimhortons;
import edu.cegepvicto.dimhortons.Menu.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class GestionDimHortonsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(
                MenuController.class.getResource("menu-view.fxml")
                //CuisineController.class.getResource("cuisine-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load(), 550, 600);
        //Ajuste la hauteur
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setHeight(screenBounds.getHeight());

        stage.setTitle("DimHortons");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) { launch(); }
}