package components.ViewInfo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ViewMainController implements Initializable {
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

    private Node viewMapRef, viewOrderHistoryRef, viewStoresRef, viewInventoryItemsRef, viewCustomersRef, storesListRef;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            System.out.println("Inside MainAppController initialize().");
            viewStoresRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewStore/ViewStore.fxml"));
            viewInventoryItemsRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewItems/ViewItems.fxml"));
            viewOrderHistoryRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewOrders/ViewOrders.fxml"));
            viewMapRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewMap/Map.fxml"));
            viewCustomersRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewCustomers/ViewCustomers.fxml"));

            loadNewPane(viewStoresRef);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void viewCustomersAction(ActionEvent event) { loadNewPane(viewCustomersRef);}

    @FXML
    void viewItemsAction(ActionEvent event) {
        loadNewPane(viewInventoryItemsRef);
    }

    @FXML
    void viewMapAction(ActionEvent event) {
        loadNewPane(viewMapRef);
    }

    @FXML
    void viewOrdersAction(ActionEvent event) {
        loadNewPane(viewOrderHistoryRef);
    }

    @FXML
    void viewStoresAction(ActionEvent event) {
        loadNewPane(viewStoresRef);
    }

    private void loadNewPane(Node paneToLoad) {
        childAnchorPane.getChildren().clear();
        childAnchorPane.getChildren().add(paneToLoad);
        AnchorPane.setBottomAnchor(paneToLoad, 0.0);
        AnchorPane.setLeftAnchor(paneToLoad, 0.0);
        AnchorPane.setRightAnchor(paneToLoad, 0.0);
        AnchorPane.setTopAnchor(paneToLoad, 0.0);
    }

}
