package components.PlaceAnOrder.ChooseStores;


import Logic.Customers.Customer;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import javafx.beans.property.*;
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
    private Label selectedStoreLabel;


    private ObservableList<Store> stores;
    private ChangeListener<Store> storeChangeListener;
    private Store selectedStore;

    private ObjectProperty<Customer> customerObjectProperty;
    private Customer customer;


    public ChooseStoreController(){
        stores = FXCollections.observableArrayList(SDMManager.getInstance().getStores());
        customerObjectProperty = new SimpleObjectProperty<>();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        chooseStoreCB.getItems().addAll(stores);

        chooseStoreCB.getSelectionModel().selectedItemProperty().addListener(
                storeChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("Selected customer is " + newValue);
                    selectedStore = newValue;
                    if (customer != null){
                        deliveryFeeLabel.setText(String.valueOf(selectedStore.getDeliveryCost(customer.getLocation())));
                        selectedStoreLabel.setText(selectedStore.getStoreName());
                        storeLocationLabel.setText(selectedStore.getLocation().toString());
                        ppkLabel.setText(String.valueOf(selectedStore.getDeliveryPpk()));
                    }
                }))
        );
//        chooseStoreCB.getSelectionModel().selectFirst();




        customerObjectProperty.addListener(((observable, oldValue, newValue) -> {
            System.out.println("ChooseStoreController customerChangeListener called!");
            if (newValue != null){
                deliveryFeeLabel.setText(String.valueOf(selectedStore.getDeliveryCost(newValue.getLocation())));
            }
        }));
    }


    public boolean hasNecessaryInformation(){
        if (selectedStore == null){
            System.out.println("Store can't be null!");
            return false;
        }
        return true;
    }

    public Store getSelectedStore() {
        return selectedStore;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void resetAllFields(){
        this.customer = null;
        this.selectedStore = null;
        selectedStoreLabel.setText("(no store selected)");
        chooseStoreCB.getSelectionModel().clearSelection();

    }


}
