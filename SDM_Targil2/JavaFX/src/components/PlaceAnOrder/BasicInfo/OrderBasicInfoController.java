package components.PlaceAnOrder.BasicInfo;


import Logic.Customers.Customer;
import Logic.Order.Cart;
import Logic.Order.eOrderType;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import components.PlaceAnOrder.ChooseItems.ChooseItemsController;
import components.PlaceAnOrder.ChooseStores.ChooseStoreController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    private ChangeListener<LocalDate> orderDateChangeListener;
    private LocalDate orderDate;

    private ChangeListener<Toggle> orderTypeChangeListener;
    private eOrderType orderType;

    private List<TitledPane> panes;
    private int openPaneNumber;
    private int numberOfPanes;


    private Store selectedStore;
    private Set<Store> storesBoughtFrom;
    private final ObservableList<Store> storeList = FXCollections.observableArrayList(Store.extractor);
    private ChangeListener<Store> storeChangeListener;

    private ScrollPane entireInventoryRef;

    private Node staticOrderRef, dynamicOrderRef;
    private ChooseStoreController staticOrderController;
    private ChooseItemsController chooseItemsController;
    private BooleanProperty isOrderComplete;

    private Cart currentCart;
    private int dummyVariable = 0;


    public OrderBasicInfoController(){
        System.out.println("Inside NewOrderContainerController Constructor...");
        sdmManager = SDMManager.getInstance();
        customers = FXCollections.observableArrayList(sdmManager.getCustomers().getCustomers());
        storesBoughtFrom = new HashSet<>();
        openPaneNumber = 0;
        //orderDate = LocalDate.now();
        currentCart = new Cart();
        isOrderComplete = new SimpleBooleanProperty(this,"isOrderComplete",false);

        try {
            FXMLLoader staticOrderLoader = new FXMLLoader();
            staticOrderLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/ChooseStores/ChooseStore.fxml"));
            staticOrderRef = staticOrderLoader.load();
            staticOrderController = staticOrderLoader.getController();

            FXMLLoader chooseItemsLoader = new FXMLLoader();
            chooseItemsLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/ChooseItems/ChooseItems.fxml"));
            dynamicOrderRef = chooseItemsLoader.load();
            chooseItemsController = chooseItemsLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize(){
        System.out.println("Inside NewOrderContainerController Initializer()...");
        chooseCustomerCB.getItems().addAll(customers);


        orderTypeGroup.selectedToggleProperty().addListener(
                orderTypeChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("ToggleGroup change detected:");
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
                    System.out.println("Selected customer is " + newValue);
                    selectedCustomer = newValue;
                }))
        );
        chooseCustomerCB.getSelectionModel().selectFirst();

        chooseDateDP.valueProperty().addListener(
                orderDateChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("Selected date: " + newValue);
                    orderDate = newValue;
                }))
        );
    }

    public DatePicker getChooseDateDP() {
        return chooseDateDP;
    }

    @FXML
    void nextButtonAction(ActionEvent event) {

//        mainPane.getChildren().clear();
//
//        if (orderType == eOrderType.STATIC_ORDER){
//            //staticOrderController.setCurrentOrderData(selectedCustomer, orderType, orderDate);
//            mainPane.getChildren().add(staticOrderRef);
//        }
//
//        else if (orderType == eOrderType.DYNAMIC_ORDER){
//            //choosingItemsController.setCurrentOrderData(selectedCustomer, orderType, orderDate);
//            mainPane.getChildren().add(dynamicOrderRef);
//        }
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

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }
}
