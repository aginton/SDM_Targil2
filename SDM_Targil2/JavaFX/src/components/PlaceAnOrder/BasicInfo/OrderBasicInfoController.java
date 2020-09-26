package components.PlaceAnOrder.BasicInfo;


import Logic.Customers.Customer;
import Logic.Order.Cart;
import Logic.Order.eOrderType;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import components.PlaceAnOrder.ChooseItems.ChooseItemsStaticOrderController;
import components.PlaceAnOrder.ChooseStores.ChooseStoreController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderBasicInfoController {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private ComboBox<Customer> chooseCustomerCB;

    @FXML
    private DatePicker chooseDateDP;

    @FXML
    private RadioButton radioStaticOrder;

    @FXML
    private RadioButton radioDynamicOrder;


    @FXML
    private ToggleGroup orderTypeGroup;


    private SDMManager sdmManager;
    private ObservableList<Customer> customers;
    private ChangeListener<Customer> customerChangeListener;
    private Customer selectedCustomer;
    private String TAG = "OrderBasicInfoController";
    private ChangeListener<LocalDate> orderDateChangeListener;
    private LocalDate orderDate;

    private ChangeListener<Toggle> orderTypeChangeListener;
    private eOrderType orderType;





    public OrderBasicInfoController(){
        sdmManager = SDMManager.getInstance();
        customers = FXCollections.observableArrayList(sdmManager.getCustomers().getCustomers());
    }

    @FXML
    private void initialize(){
        setUpFields();
    }


    public DatePicker getChooseDateDP() {
        return chooseDateDP;
    }

    public eOrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(eOrderType orderType) {
        this.orderType = orderType;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public boolean hasNecessaryInformation(){
        if (orderDate == null){
            System.out.println("Date can't be null!");
            return false;
        }
        if (selectedCustomer == null){
            System.out.println("Customer can't be null!");
            return false;
        }
        return true;
    }

    public void resetAllFields(){
        chooseDateDP.getEditor().clear();
    }

    public void refresh() {
        System.out.println(TAG + " - refresh()");
        customers.clear();
        chooseCustomerCB.getItems().clear();
        sdmManager = SDMManager.getInstance();
        customers = FXCollections.observableArrayList(sdmManager.getCustomers().getCustomers());
        setUpFields();
    }

    private void setUpFields(){
        chooseCustomerCB.getItems().addAll(customers);


        orderTypeGroup.selectedToggleProperty().addListener(
                orderTypeChangeListener = (((observable, oldValue, newValue) -> {
                    //System.out.println("ToggleGroup change detected:");
                    if (newValue == radioDynamicOrder) {
                        orderType = eOrderType.DYNAMIC_ORDER;
                    }
                    else if (newValue == radioStaticOrder) {
                        orderType = eOrderType.STATIC_ORDER;
                    }
                }))
        );

        radioStaticOrder.setSelected(true); //Set default orderType as Static Order

        chooseCustomerCB.getSelectionModel().selectedItemProperty().addListener(
                customerChangeListener = (((observable, oldValue, newValue) -> {
                    //System.out.println("Selected customer is " + newValue);
                    selectedCustomer = newValue;
                }))
        );
        chooseCustomerCB.getSelectionModel().selectFirst();

        chooseDateDP.valueProperty().addListener(
                orderDateChangeListener = (((observable, oldValue, newValue) -> {
                    //System.out.println("Selected date: " + newValue);
                    orderDate = newValue;
                }))
        );
    }
}
