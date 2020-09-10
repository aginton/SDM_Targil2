package components.viewInfoMenu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ViewMenuController implements Initializable {

    @FXML
    private AnchorPane outerAnchorPane;

    @FXML
    private AnchorPane leftAnchorPane;

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
    private AnchorPane rightAnchorPane;



    private AnchorPane storeListRef, itemsListRef, mapRef, orderHistoryRef;

    @FXML
    void viewCustomersAction(ActionEvent event) {

    }

    @FXML
    void viewItemsAction(ActionEvent event) {
        System.out.println("Calling setCenterPane(storeListRef):");
        rightAnchorPane.getChildren().clear();
        rightAnchorPane.getChildren().add(itemsListRef);
        AnchorPane.setBottomAnchor(storeListRef, 0.0);
        AnchorPane.setLeftAnchor(storeListRef, 0.0);
        AnchorPane.setRightAnchor(storeListRef, 0.0);
        AnchorPane.setTopAnchor(storeListRef, 0.0);
    }

    @FXML
    void viewMapAction(ActionEvent event) {
        System.out.println("Calling setCenterPane(map):");
        rightAnchorPane.getChildren().clear();
        rightAnchorPane.getChildren().add(mapRef);
        AnchorPane.setBottomAnchor(storeListRef, 0.0);
        AnchorPane.setLeftAnchor(storeListRef, 0.0);
        AnchorPane.setRightAnchor(storeListRef, 0.0);
        AnchorPane.setTopAnchor(storeListRef, 0.0);
    }

    @FXML
    void viewOrdersAction(ActionEvent event) {
        System.out.println("Calling setCenterPane(orderHistoryRef):");
        rightAnchorPane.getChildren().clear();
        rightAnchorPane.getChildren().add(orderHistoryRef);
        AnchorPane.setBottomAnchor(storeListRef, 0.0);
        AnchorPane.setLeftAnchor(storeListRef, 0.0);
        AnchorPane.setRightAnchor(storeListRef, 0.0);
        AnchorPane.setTopAnchor(storeListRef, 0.0);
    }

    @FXML
    void viewStoresAction(ActionEvent event) {
        System.out.println("Calling setCenterPane(storeListRef):");
        rightAnchorPane.getChildren().clear();
        rightAnchorPane.getChildren().add(storeListRef);
        AnchorPane.setBottomAnchor(storeListRef, 0.0);
        AnchorPane.setLeftAnchor(storeListRef, 0.0);
        AnchorPane.setRightAnchor(storeListRef, 0.0);
        AnchorPane.setTopAnchor(storeListRef, 0.0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("Inside MainAppController initialize().");
            storeListRef = FXMLLoader.load(getClass().getResource("/components/viewInfoMenu/StoreView/StoreUI.fxml"));
            itemsListRef = FXMLLoader.load(getClass().getResource("/components/viewInfoMenu/ItemsView/ItemsUI.fxml"));
            orderHistoryRef = FXMLLoader.load(getClass().getResource("/components/viewInfoMenu/OrderView/OrderView.fxml"));
            mapRef = FXMLLoader.load(getClass().getResource("/components/viewInfoMenu/MapView/MapView.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
