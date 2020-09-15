package components.ViewInfo.ViewOrders;

import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Order.Order;
import Logic.SDM.SDMManager;
import components.ViewInfo.ViewOrders.SingleOrder.SingleOrderViewController;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.List;

public class OrdersViewController {
    @FXML
    private AnchorPane orderViewAnchorPane;

    @FXML
    private FlowPane orderHistoryFlowpane;


    private ObservableList<Order> orders;



    public void initialize(){

        System.out.println("Inside OrderViewController");
        updateOrderHistoryView();
    }

    public void updateOrderHistoryView() {
            try {
                for (Order order: SDMManager.getInstance().getOrderHistory().getOrders()){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/ViewInfo/ViewOrders/SingleOrder/SingleOrderView.fxml"));
                Node n = loader.load();
                SingleOrderViewController controller = loader.getController();
                controller.setOrder(order);
                orderHistoryFlowpane.getChildren().add(n);
            }
            }catch (IOException e) {
                e.printStackTrace();
        }
    }
}
