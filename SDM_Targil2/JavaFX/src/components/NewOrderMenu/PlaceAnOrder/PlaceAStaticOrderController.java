package components.NewOrderMenu.PlaceAnOrder;


import Logic.Customers.Customer;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PlaceAStaticOrderController implements Initializable {

    @FXML
    private AnchorPane placeAStaticOrderRootPane;

    @FXML
    private ComboBox<Store> chooseStoreCB;

    @FXML
    private Label deliveryFeeLabel;

    @FXML
    private Label storeLocationLabel;

    @FXML
    private Label ppkLabel;

    @FXML
    private Button nextButton;

    @FXML
    private Button backButton;


    private ObservableList<Store> stores;
    private ChangeListener<Store> storeChangeListener;
    private Store selectedStore;
    private ChoosingItemsController choosingItemsController;

    private Customer customer;


    public PlaceAStaticOrderController(){
        stores = FXCollections.observableArrayList(SDMManager.getInstance().getStores());
    }


    public void setCustomer(Customer c){
        customer = c;
        updateStoreBasicInfo(selectedStore);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        chooseStoreCB.getItems().addAll(stores);

        chooseStoreCB.getSelectionModel().selectedItemProperty().addListener(
                storeChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("Selected customer is " + newValue);
                    selectedStore = newValue;
                    updateStoreBasicInfo(selectedStore);
                }))
        );
        chooseStoreCB.getSelectionModel().selectFirst();


    }

    private void updateStoreBasicInfo(Store selectedStore) {
        storeLocationLabel.setText(selectedStore.getLocation().toString());
        ppkLabel.setText(String.valueOf(selectedStore.getDeliveryPpk()));
        if (customer != null)
            deliveryFeeLabel.setText(String.valueOf(selectedStore.getDeliveryCost(customer.getLocation())));
        else
            deliveryFeeLabel.setText("error!");
    }

    @FXML
    void backButtonAction(ActionEvent event) {

    }

    @FXML
    void nextButtonAction(ActionEvent event) {

        placeAStaticOrderRootPane.getChildren().clear();
        System.out.println("going to choose items");

        try {
            FXMLLoader cartItemsLoader = new FXMLLoader();
            cartItemsLoader.setLocation(getClass().getResource("/components/NewOrderMenu/PlaceAnOrder/ChoosingItems.fxml"));
            Node parent = cartItemsLoader.load();
            //After setting the scene, we can access the controller and call a method
            choosingItemsController = cartItemsLoader.getController();
            choosingItemsController.initData(selectedStore);

            placeAStaticOrderRootPane.getChildren().add(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//
//    private void loadItemsForStore(Store selectedStore) {
//        try {
//            FXMLLoader cartItemsLoader = new FXMLLoader();
//            cartItemsLoader.setLocation(getClass().getResource("/components/NewOrderMenu/PlaceAnOrder/ChoosingItems.fxml"));
//            Parent parent = cartItemsLoader.load();
//            //After setting the scene, we can access the controller and call a method
//            choosingItemsController = cartItemsLoader.getController();
//            choosingItemsController.initData(selectedStore);
//
//            itemsAnchorPane.getChildren().clear();
//            itemsAnchorPane.getChildren().add(parent);
//            AnchorPane.setTopAnchor(parent,0.0);
//            AnchorPane.setRightAnchor(parent,0.0);
//            AnchorPane.setLeftAnchor(parent,0.0);
//            AnchorPane.setBottomAnchor(parent,0.0);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
