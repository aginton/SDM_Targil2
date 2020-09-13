package components.ViewInfo.ViewOrders;

import Logic.Inventory.InventoryItem;
import Logic.Order.Order;
import Logic.SDM.SDMManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import java.util.List;

public class OrdersViewController {
    @FXML
    private AnchorPane orderViewAnchorPane;

    @FXML
    private FlowPane orderHistoryFlowpane;

    private SDMManager sdmManager;



    private List<Order> storeOrders;

    public void initialize(){
        sdmManager = SDMManager.getInstance();

        System.out.println("Inside OrderViewController");
        for (int i=0; i<4; i++){
            System.out.println("Creating rectangle " + (i+1));
            String buttonStr = String.valueOf(i);
            orderHistoryFlowpane.getChildren().add(new Button(buttonStr));
        }
    }
}
