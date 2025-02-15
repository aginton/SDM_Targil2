package components.PlaceAnOrder.SuccessOrError;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;

public class SuccessPopUpController {
    @FXML
    private Label success_error_label;
    @FXML
    private AnchorPane root;


    @FXML
    void onOKButtonClicked(ActionEvent event) {
        Stage stage = (Stage)root.getScene().getWindow();
        stage.close();
    }

    public void setSuccess_error_label_Text(String message){
        success_error_label.setText(message);
    }
}
