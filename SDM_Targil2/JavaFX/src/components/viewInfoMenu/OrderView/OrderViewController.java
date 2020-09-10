package components.viewInfoMenu.OrderView;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Rectangle;

public class OrderViewController {
    @FXML
    private AnchorPane orderViewAnchorPane;

    @FXML
    private FlowPane orderHistoryFlowpane;

    public void initialize(){
        System.out.println("Inside OrderViewController");
        for (int i=0; i<4; i++){
            System.out.println("Creating rectangle " + (i+1));
            String buttonStr = String.valueOf(i);
            orderHistoryFlowpane.getChildren().add(new Button(buttonStr));
        }
    }
}
