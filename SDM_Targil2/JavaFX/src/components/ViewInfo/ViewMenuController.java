package components.ViewInfo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ViewMenuController implements Initializable {
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

    private AnchorPane mapRef, orderHistoryRef, viewStoresRef;
    private TableView itemsListRef;
    private SplitPane storeListRef;


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
        childAnchorPane.getChildren().clear();
        childAnchorPane.getChildren().add(orderHistoryRef);
        AnchorPane.setBottomAnchor(orderHistoryRef, 0.0);
        AnchorPane.setLeftAnchor(orderHistoryRef, 0.0);
        AnchorPane.setRightAnchor(orderHistoryRef, 0.0);
        AnchorPane.setTopAnchor(orderHistoryRef, 0.0);
    }

    @FXML
    void viewStoresAction(ActionEvent event) {
        System.out.println("Calling setCenterPane(storeListRef):");
        childAnchorPane.getChildren().clear();
        childAnchorPane.getChildren().add(viewStoresRef);
        AnchorPane.setBottomAnchor(viewStoresRef, 0.0);
        AnchorPane.setLeftAnchor(viewStoresRef, 0.0);
        AnchorPane.setRightAnchor(viewStoresRef, 0.0);
        AnchorPane.setTopAnchor(viewStoresRef, 0.0);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            System.out.println("Inside MainAppController initialize().");
            viewStoresRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewStore/ViewStore.fxml"));
            itemsListRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewItems/ViewItems.fxml"));
            orderHistoryRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewOrders/OrdersView.fxml"));
            mapRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewMap/MapView.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
