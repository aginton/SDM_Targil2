package components.ViewInfo;

import Logic.Order.Order;
import Logic.Order.OrderChangeInterface;
import components.ViewInfo.ViewOrders.OrdersViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ViewMenuController implements Initializable, OrderChangeInterface {
    @FXML
    private GridPane rootGridpane;

    @FXML
    private Button viewStoresButton;

    @FXML
    private Button viewItemsButton;

    @FXML
    private Button viewOrdersButton;

    @FXML
    private Button viewCustomersButton;

    @FXML
    private Button viewMapButton;

    @FXML
    private AnchorPane childAnchorPane;

    private AnchorPane mapRef, orderHistoryRef, viewStoresRef, itemsListRef, storesListRef;
//    private TableView itemsListRef;
    //private SplitPane storeListRef;

    private OrdersViewController ordersViewController;


    public ViewMenuController() {

    }

    @FXML
    void viewCustomersAction(ActionEvent event) {

    }

    @FXML
    void viewItemsAction(ActionEvent event) {
        System.out.println("Calling setCenterPane(storeListRef):");
        childAnchorPane.getChildren().clear();
        childAnchorPane.getChildren().add(itemsListRef);
        AnchorPane.setBottomAnchor(itemsListRef, 0.0);
        AnchorPane.setLeftAnchor(itemsListRef, 0.0);
        AnchorPane.setRightAnchor(itemsListRef, 0.0);
        AnchorPane.setTopAnchor(itemsListRef, 0.0);
    }

    @FXML
    void viewMapAction(ActionEvent event) {
        System.out.println("Calling setCenterPane(map):");
        childAnchorPane.getChildren().clear();
        childAnchorPane.getChildren().add(mapRef);
        AnchorPane.setBottomAnchor(mapRef, 0.0);
        AnchorPane.setLeftAnchor(mapRef, 0.0);
        AnchorPane.setRightAnchor(mapRef, 0.0);
        AnchorPane.setTopAnchor(mapRef, 0.0);
    }

    @FXML
    void viewOrdersAction(ActionEvent event) {
        System.out.println("Calling setCenterPane(orderHistoryRef):");
        //ordersViewController.updateOrderHistoryView();

        loadNewPane(orderHistoryRef);

    }

    @FXML
    void viewStoresAction(ActionEvent event) {
        System.out.println("Calling setCenterPane(storeListRef):");
        loadNewPane(viewStoresRef);
    }

    private void loadNewPane(Pane paneToLoad) {
        childAnchorPane.getChildren().clear();
        childAnchorPane.getChildren().add(paneToLoad);
        AnchorPane.setBottomAnchor(paneToLoad, 0.0);
        AnchorPane.setLeftAnchor(paneToLoad, 0.0);
        AnchorPane.setRightAnchor(paneToLoad, 0.0);
        AnchorPane.setTopAnchor(paneToLoad, 0.0);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            System.out.println("Inside MainAppController initialize().");
            viewStoresRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewStore/ViewStore.fxml"));
            itemsListRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewItems/ViewItems.fxml"));

            FXMLLoader ordersLoader = new FXMLLoader();
            ordersLoader.setLocation(getClass().getResource("/components/ViewInfo/ViewOrders/OrdersView.fxml"));
            orderHistoryRef = ordersLoader.load();
            ordersViewController = ordersLoader.getController();
            mapRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewMap/MapView.fxml"));

            loadNewPane(viewStoresRef);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void orderWasAdded(Order order) {
        System.out.println("An order with id=" + order.getOrderId() + " was added!");
    }
}
