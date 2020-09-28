package components.ViewInfo.ViewOrders;

import Logic.Order.Order;
import Logic.Order.OrderChangeListener;
import Logic.SDM.SDMManager;
import components.ViewInfo.ViewOrders.SingleOrder.SingleOrderViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import java.io.IOException;

public class ViewOrdersController implements OrderChangeListener {
    private static final String TAG = "ViewOrdersController";
    @FXML
    private AnchorPane orderViewAnchorPane;

    @FXML
    private FlowPane orderHistoryFlowpane;


    private ObservableList<Order> orders;

    public ViewOrdersController(){
//        orders = SDMManager.getInstance().getOrderHistory().getOrdersObservableList();
        orders = FXCollections.observableArrayList(SDMManager.getInstance().getOrderHistory().getOrders());
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

    public void refresh() {
        System.out.println(TAG + " - refresh()");
        orderHistoryFlowpane.getChildren().clear();
        orders.clear();
        //orders = SDMManager.getInstance().getOrderHistory().getOrdersObservableList();
        orders = FXCollections.observableArrayList(SDMManager.getInstance().getOrderHistory().getOrders());
        SDMManager.getInstance().getOrderHistory().addOrdersChangeListener(this);

    }

}
