package components.PlaceAnOrder.ChooseStores;


import Logic.Customers.Customer;
import Logic.Order.eOrderType;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import components.PlaceAnOrder.ChooseItems.ChooseItemsController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ChooseStoreController implements Initializable {

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
    private ChooseItemsController chooseItemsController;
    private Customer customer;
    private eOrderType orderType;
    private LocalDate orderDate;
    private BooleanProperty isOrderComplete;



    public ChooseStoreController(){
        stores = FXCollections.observableArrayList(SDMManager.getInstance().getStores());
    }



    public void setCurrentOrderData(Customer c, eOrderType orderType, LocalDate orderDate, BooleanProperty isOrderComplete){
        this.customer = c;
        this.orderType = orderType;
        this.orderDate = orderDate;
        this.isOrderComplete = isOrderComplete;
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
//
//        placeAStaticOrderRootPane.getChildren().clear();
//        System.out.println("going to choose items");
//
//        try {
//            FXMLLoader cartItemsLoader = new FXMLLoader();
//            cartItemsLoader.setLocation(getClass().getResource("/components/NewOrderMenu/PlaceAnOrder/ChooseItems.fxml"));
//            Node parent = cartItemsLoader.load();
//            //After setting the scene, we can access the controller and call a method
//            choosingItemsController = cartItemsLoader.getController();
//            choosingItemsController.setDataForStaticOrder(selectedStore, customer, orderType, orderDate);
//
//            placeAStaticOrderRootPane.getChildren().add(parent);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void setCurrentOrderData(Customer customer, eOrderType orderType, LocalDate orderDate) {
        this.customer = customer;
        this.orderType = orderType;
        this.orderDate = orderDate;
        updateStoreBasicInfo(selectedStore);
    }

    public Store getSelectedStore() {
        return selectedStore;
    }
}
