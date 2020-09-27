package components.PlaceAnOrder.SuccessOrError;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;


public class ConfirmBox {

    boolean answer;

    public boolean display(String title, String message, String content){
        answer = false;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(message);
        alert.setTitle(title);
        alert.setContentText(content);

        ImageView imageView = new ImageView(this.getClass().getResource("/resources/sign_warning.png").toString());
        imageView.setFitHeight(64);
        imageView.setFitWidth(64);
        alert.setGraphic(imageView);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            answer = true;
        }
        return answer;
    }
}
