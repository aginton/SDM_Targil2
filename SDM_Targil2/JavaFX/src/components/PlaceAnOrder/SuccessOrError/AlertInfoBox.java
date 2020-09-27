package components.PlaceAnOrder.SuccessOrError;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertInfoBox {

    Boolean ans;

    public void display(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);// line 1
        alert.setTitle(title);// line 2
        alert.setHeaderText(header);// line 3
        alert.setContentText(content);// line 4
        alert.showAndWait();
    }
}
