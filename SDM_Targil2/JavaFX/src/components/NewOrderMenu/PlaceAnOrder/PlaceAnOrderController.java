package components.NewOrderMenu.PlaceAnOrder;


import Logic.Customers.Customer;
import Logic.Order.Cart;
import Logic.Order.eOrderType;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import components.NewOrderMenu.ChooseItemsView.ChooseItemsFromStoreController;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaceAnOrderController {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private ComboBox<Customer> chooseCustomerCB;

    @FXML
    private DatePicker chooseDateCB;

    @FXML
    private RadioButton radioStaticOrder;

    @FXML
    private RadioButton radioDynamicOrder;

    @FXML
    private Button nextButton;

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
    private PlaceAStaticOrderController staticOrderController;
    private BooleanProperty isOrderComplete;

    private Cart currentCart;
    private int dummyVariable = 0;


    public PlaceAnOrderController(){
        System.out.println("Inside NewOrderContainerController Constructor...");
        sdmManager = SDMManager.getInstance();
        customers = FXCollections.observableArrayList(sdmManager.getCustomers().getCustomers());
        storesBoughtFrom = new HashSet<>();
        openPaneNumber = 0;
        orderDate = LocalDate.now();
        currentCart = new Cart();
        isOrderComplete = new SimpleBooleanProperty(this,"isOrderComplete",false);

        try {
            FXMLLoader staticOrderLoader = new FXMLLoader();
            staticOrderLoader.setLocation(getClass().getResource("/components/NewOrderMenu/PlaceAnOrder/PlaceAStaticOrder.fxml"));
            staticOrderRef = staticOrderLoader.load();
            staticOrderController = staticOrderLoader.getController();

            dynamicOrderRef = FXMLLoader.load(getClass().getResource("/components/NewOrderMenu/PlaceAnOrder/ChoosingItems.fxml"));
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

        chooseDateCB.valueProperty().addListener(
                orderDateChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("Selected date: " + newValue);
                    orderDate = newValue;
                }))
        );
    }



    @FXML
    void nextButtonAction(ActionEvent event) {

        mainPane.getChildren().clear();

        if (orderType == eOrderType.STATIC_ORDER){
            staticOrderController.setCustomer(selectedCustomer);
            mainPane.getChildren().add(staticOrderRef);
        }

        else if (orderType == eOrderType.DYNAMIC_ORDER)
            mainPane.getChildren().add(dynamicOrderRef);
    }

    public boolean getIsOrderComplete() {
        return isOrderComplete.get();
    }

    public BooleanProperty isOrderCompleteProperty() {
        return isOrderComplete;
    }

    public void setIsOrderComplete(boolean isOrderComplete) {
        this.isOrderComplete.set(isOrderComplete);
    }

    public void bindIsCompleteOrder(BooleanProperty otherBooleanProperty){
        isOrderComplete.bindBidirectional(otherBooleanProperty);
    }

}
