package components.ViewInfo;

import components.ViewInfo.ViewItems.ViewItemsController;
import components.ViewInfo.ViewMap.MapController;
import components.ViewInfo.ViewOrders.ViewOrdersController;
import components.ViewInfo.ViewStore.ViewStoreController;
import components.ViewInfo.ViewCustomers.ViewCustomersController;
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
    private ViewStoreController viewStoreController;
    private ViewOrdersController viewOrdersController;
    private ViewItemsController viewItemsController;
    private ViewCustomersController viewCustomersController;
    private MapController viewMapController;
    private String TAG = "ViewMainController";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("Inside MainAppController initialize().");
            FXMLLoader viewStoresLoader = new FXMLLoader();
            viewStoresLoader.setLocation(getClass().getResource("/components/ViewInfo/ViewStore/ViewStore.fxml"));
            viewStoresRef = viewStoresLoader.load();
            viewStoreController = viewStoresLoader.getController();

            FXMLLoader viewItemsLoader = new FXMLLoader();
            viewItemsLoader.setLocation(getClass().getResource("/components/ViewInfo/ViewItems/ViewItems.fxml"));
            viewInventoryItemsRef = viewItemsLoader.load();
            viewItemsController = viewItemsLoader.getController();

            FXMLLoader viewOrdersLoader = new FXMLLoader();
            viewOrdersLoader.setLocation(getClass().getResource("/components/ViewInfo/ViewOrders/ViewOrders.fxml"));
            viewOrderHistoryRef = viewOrdersLoader.load();
            viewOrdersController = viewOrdersLoader.getController();

            FXMLLoader viewCustomersLoader = new FXMLLoader();
            viewCustomersLoader.setLocation(getClass().getResource("/components/ViewInfo/ViewCustomers/ViewCustomers.fxml"));
            viewCustomersRef = viewCustomersLoader.load();
            viewCustomersController = viewCustomersLoader.getController();

            FXMLLoader viewMapLoader = new FXMLLoader();
            viewMapLoader.setLocation(getClass().getResource("/components/ViewInfo/ViewMap/Map.fxml"));
            viewMapRef = viewMapLoader.load();
            viewMapController = viewMapLoader.getController();

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
    void viewOrdersAction(ActionEvent event) { loadNewPane(viewOrderHistoryRef); }

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

    public void resetAllFields() {

    }

    public void refreshOthers() {
        System.out.println("\n" + TAG + " - refreshOthers() called!");
        viewStoreController.refresh();
        viewItemsController.refresh();
        viewOrdersController.refresh();
        viewCustomersController.refresh();
        viewMapController.refresh();
    }
}
