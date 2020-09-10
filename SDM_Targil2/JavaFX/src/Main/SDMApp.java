package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class SDMApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Super Duper Market");
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("MainApp.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());

        Scene scene = new Scene(root);
        final String cssURL = this.getClass().getResource("MainAppCSS.css").toExternalForm();
        scene.getStylesheets().add(cssURL);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
