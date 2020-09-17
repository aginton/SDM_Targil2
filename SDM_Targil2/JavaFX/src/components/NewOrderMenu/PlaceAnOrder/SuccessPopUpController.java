package components.NewOrderMenu.PlaceAnOrder;

import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;

public class SuccessPopUpController {
    @FXML
    private AnchorPane root;


    @FXML
    void onOKButtonClicked(ActionEvent event) {
        Stage stage = (Stage)root.getScene().getWindow();
        stage.close();
    }
}
