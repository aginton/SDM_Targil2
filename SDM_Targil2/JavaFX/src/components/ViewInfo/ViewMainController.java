package components.ViewInfo;

import Logic.Order.Order;
import Logic.Order.OrderChangeInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

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

    private AnchorPane viewMapRef, viewOrderHistoryRef, viewStoresRef, viewInventoryItemsRef, storesListRef;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            System.out.println("Inside MainAppController initialize().");
            viewStoresRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewStore/ViewStore.fxml"));
            viewInventoryItemsRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewItems/ViewItems.fxml"));
            viewOrderHistoryRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewOrders/ViewOrders.fxml"));
            viewMapRef = FXMLLoader.load(getClass().getResource("/components/ViewInfo/ViewMap/ViewMap.fxml"));

            loadNewPane(viewStoresRef);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void viewCustomersAction(ActionEvent event) {

    }

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

    private void loadNewPane(Pane paneToLoad) {
        childAnchorPane.getChildren().clear();
        childAnchorPane.getChildren().add(paneToLoad);
        AnchorPane.setBottomAnchor(paneToLoad, 0.0);
        AnchorPane.setLeftAnchor(paneToLoad, 0.0);
        AnchorPane.setRightAnchor(paneToLoad, 0.0);
        AnchorPane.setTopAnchor(paneToLoad, 0.0);
    }

}
