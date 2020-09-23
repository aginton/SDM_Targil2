package components.PlaceAnOrder.SuccessOrError;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SuccessPopUp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/components/PlaceAnOrder/SuccessOrError/SuccessPopUp.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
