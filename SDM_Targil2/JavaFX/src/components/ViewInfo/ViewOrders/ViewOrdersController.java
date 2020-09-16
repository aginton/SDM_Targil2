package components.ViewInfo.ViewOrders;

import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Order.Order;
import Logic.Order.OrderChangeInterface;
import Logic.SDM.SDMManager;
import components.ViewInfo.ViewOrders.SingleOrder.SingleOrderViewController;
import javafx.beans.InvalidationListener;
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

public class ViewOrdersController implements OrderChangeInterface {
    @FXML
    private AnchorPane orderViewAnchorPane;

    @FXML
    private FlowPane orderHistoryFlowpane;


    private ObservableList<Order> orders;

    public ViewOrdersController(){
        orders = SDMManager.getInstance().getOrderHistory().getOrdersObservableList();
        SDMManager.getInstance().getOrderHistory().addOrdersChangeListener(this);
    }

    @Override
    public void orderWasAdded(Order order) {
        System.out.println("OrdersViewController sees order was added!");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/ViewInfo/ViewOrders/SingleOrder/SingleOrderView.fxml"));
            Node n = loader.load();
            SingleOrderViewController controller = loader.getController();
            controller.setOrder(order);
            orderHistoryFlowpane.getChildren().add(n);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void initialize(){
////        System.out.println("Inside OrderViewController Initializer");
////        orders.addListener((InvalidationListener) observable -> updateOrderHistoryView());
//
//    }

//    public void updateOrderHistoryView() {
//            try {
//                orderHistoryFlowpane.getChildren().clear();
//                for (Order order: orders){
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/ViewInfo/ViewOrders/SingleOrder/SingleOrderView.fxml"));
//                Node n = loader.load();
//                SingleOrderViewController controller = loader.getController();
//                controller.setOrder(order);
//                orderHistoryFlowpane.getChildren().add(n);
//            }
//            }catch (IOException e) {
//                e.printStackTrace();
//        }
//    }


}
