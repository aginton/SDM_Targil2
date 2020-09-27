package components.PlaceAnOrder.SuccessOrError;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertConfirmation {

    static Boolean ans;

    public static Boolean display(String Message){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);// line 1
        alert.setTitle("Order In Progress Warning");// line 2
        alert.setHeaderText("Please Confirm!");// line 3
        alert.setContentText(Message);// line 4
        //alert.showAndWait(); // line 5

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get()==ButtonType.OK){
            ans = true;
        } else{
            ans = false;
        }

        return ans;
    }
}
