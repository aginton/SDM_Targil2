package components.NewOrderMenu;

import Logic.Customers.Customer;
import Logic.Order.Cart;
import Logic.Order.Order;
import Logic.Order.eOrderType;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import components.NewOrderMenu.ChooseItemsView.ChooseItemsFromStoreController;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class NewOrderContainerController {

    @FXML
    private TitledPane customerPane;

    @FXML
    private Accordion accordion;

    @FXML
    private ComboBox<Customer> chooseCustomerComboBox;

    @FXML
    private TitledPane datePane;

    @FXML
    private DatePicker chooseDateDatePicker;

    @FXML
    private TitledPane orderTypePane;

    @FXML
    private RadioButton staticOrderRadioButton;

    @FXML
    private ToggleGroup orderTypeGroup;

    @FXML
    private RadioButton dynamicOrderRadioButton;

    @FXML
    private TitledPane storePane;

    @FXML
    private AnchorPane itemsAnchorPane;

    @FXML
    private AnchorPane salesAnchorPane;

    @FXML
    private TitledPane itemsPane;

    @FXML
    private TitledPane salesPane;

    @FXML
    private TitledPane confirmPane;

    @FXML
    private ListView storesListView;

    @FXML
    private Label chooseCustomerLabel;

    @FXML
    private Label chooseDateLabel;

    @FXML
    private Label chooseOrderTypeLabel;

    @FXML
    private Label chooseSalesLabel;

    @FXML
    private Label orderSubtotalLabel;

    @FXML
    private Label deliveryFeeLabel;

    @FXML
    private Label totalCostLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button confirmButton;

    private SDMManager sdmManager;

//    private List<Label> labels;
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
    ChooseItemsFromStoreController chooseItemsFromStoreController;
    private BooleanProperty isOrderComplete;

    private Cart currentCart;
    private int dummyVariable = 0;


    public NewOrderContainerController(){
        System.out.println("Inside NewOrderContainerController Constructor...");
        sdmManager = SDMManager.getInstance();
        customers = FXCollections.observableArrayList(sdmManager.getCustomers().getCustomers());
        storesBoughtFrom = new HashSet<>();
        openPaneNumber = 0;
        orderDate = LocalDate.now();
        currentCart = new Cart();
        isOrderComplete = new SimpleBooleanProperty(this,"isOrderComplete",false);

//        isOrderComplete.addListener(((observable, oldValue, newValue) -> {
//            System.out.println("NewOrderContainerController: isOrderComplete property changed");
//        }));
    }

    @FXML
    private void initialize(){
        System.out.println("Inside NewOrderContainerController Initializer()...");
        chooseCustomerComboBox.getItems().addAll(customers);
        accordion.setExpandedPane(customerPane);
        panes = Arrays.asList(customerPane,datePane,orderTypePane,storePane,itemsPane,salesPane,confirmPane);
        numberOfPanes = panes.size();
        for (int i=1; i<numberOfPanes; i++){
            panes.get(i).setDisable(true);
        }

        backButton.disableProperty().bind(accordion.expandedPaneProperty().isEqualTo(customerPane));
        nextButton.disableProperty().bind(accordion.expandedPaneProperty().isEqualTo(confirmPane));
        confirmButton.disableProperty().bind(accordion.expandedPaneProperty().isNotEqualTo(confirmPane));
        sdmManager.fillSampleData(storeList);
        storesListView.setItems(storeList);




        orderTypeGroup.selectedToggleProperty().addListener(
                orderTypeChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("ToggleGroup change detected:");
                    if (newValue == dynamicOrderRadioButton) {
                        orderType = eOrderType.DYNAMIC_ORDER;
                    }
                    else if (newValue == staticOrderRadioButton) {
                        orderType = eOrderType.STATIC_ORDER;
                    }
                }))
        );

        staticOrderRadioButton.setSelected(true); //Set default orderType as Static Order


        storesListView.getSelectionModel().selectedItemProperty().addListener(
                storeChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("Selected store: " + newValue);
                    //newValue can be null if nothing is selected
                    selectedStore = newValue;
                }))
        );
        storesListView.getSelectionModel().selectFirst();



        chooseCustomerComboBox.getSelectionModel().selectedItemProperty().addListener(
                customerChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("Selected customer is " + newValue);
                    selectedCustomer = newValue;
                }))
        );
        chooseCustomerComboBox.getSelectionModel().selectFirst();

        chooseDateDatePicker.valueProperty().addListener(
                orderDateChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("Selected date: " + newValue);
                    orderDate = newValue;
                }))
        );
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


    @FXML
    void backBtnAction(ActionEvent event) {

        if (panes.get(openPaneNumber)==itemsPane){
            if (orderType == eOrderType.DYNAMIC_ORDER){
                panes.get(openPaneNumber).setDisable(true);
                panes.get(--openPaneNumber).setDisable(false);
                accordion.setExpandedPane(panes.get(openPaneNumber));
            }
        }

        panes.get(openPaneNumber).setDisable(true);
        panes.get(--openPaneNumber).setDisable(false);
        accordion.setExpandedPane(panes.get(openPaneNumber));
    }

    @FXML
    void confirmButtonAction(ActionEvent event) {
        System.out.println("Confirm button pressed");
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Date date = Date.from(orderDate.atStartOfDay(defaultZoneId).toInstant());

        Order order = new Order(selectedCustomer.getCustomerLocation(),
                date,
                6,
                currentCart,
                storesBoughtFrom,
                orderType);


        if (orderType == eOrderType.STATIC_ORDER)
            sdmManager.addNewStaticOrder(selectedStore, order);

        resetNewOrderContainerToDefaultState();
        setIsOrderComplete(true);
    }

    private void resetNewOrderContainerToDefaultState() {
        openPaneNumber = 0;
        panes.get(openPaneNumber).setDisable(false);
        accordion.setExpandedPane(panes.get(openPaneNumber));
    }

    public void bindIsCompleteOrder(BooleanProperty otherBooleanProperty){
        isOrderComplete.bindBidirectional(otherBooleanProperty);
    }

    @FXML
    void nextBtnAction(ActionEvent event) {
        System.out.println("DummyVariable=" + ++dummyVariable + ", isOrderCompleteProperty()=" + getIsOrderComplete() +
                ", selected store is: " + selectedStore.getStoreName());

        if (panes.get(openPaneNumber)==orderTypePane){

            if (orderType == eOrderType.DYNAMIC_ORDER){
                panes.get(openPaneNumber).setDisable(true);
                panes.get(++openPaneNumber).setDisable(false);
                accordion.setExpandedPane(panes.get(openPaneNumber));
                loadAllInventoryItems();
            }
        }
        if (panes.get(openPaneNumber)==storePane){
            if (orderType == eOrderType.STATIC_ORDER){
                storesBoughtFrom.add(selectedStore);
                loadItemsForStore(selectedStore);
            }
        }

        if (panes.get(openPaneNumber)==itemsPane){
            currentCart = chooseItemsFromStoreController.getCurrentCart();
            System.out.println("New Order container found :" + currentCart);
        }
        if (panes.get(openPaneNumber)==salesPane){
        }

        panes.get(openPaneNumber).setDisable(true);
        panes.get(++openPaneNumber).setDisable(false);
        accordion.setExpandedPane(panes.get(openPaneNumber));
    }

    private void loadItemsForStore(Store selectedStore) {
        try {
            FXMLLoader cartItemsLoader = new FXMLLoader();
            cartItemsLoader.setLocation(getClass().getResource("/components/NewOrderMenu/ChooseItemsView/ChooseItemsFromStore.fxml"));
            Parent gridPaneParent = cartItemsLoader.load();
            //After setting the scene, we can access the controller and call a method
            chooseItemsFromStoreController = cartItemsLoader.getController();
            chooseItemsFromStoreController.initData(selectedStore);

            itemsAnchorPane.getChildren().clear();
            itemsAnchorPane.getChildren().add(gridPaneParent);
            AnchorPane.setTopAnchor(gridPaneParent,0.0);
            AnchorPane.setRightAnchor(gridPaneParent,0.0);
            AnchorPane.setLeftAnchor(gridPaneParent,0.0);
            AnchorPane.setBottomAnchor(gridPaneParent,0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadAllInventoryItems() {
        try {
            System.out.println("Going to show entire inventory");
            entireInventoryRef = FXMLLoader.load(getClass().getResource("/components/NewOrderMenu/ChooseItemsView/ChooseFromEntireInventory.fxml"));
            itemsAnchorPane.getChildren().clear();
            itemsAnchorPane.getChildren().add(entireInventoryRef);
            AnchorPane.setTopAnchor(entireInventoryRef,0.0);
            AnchorPane.setRightAnchor(entireInventoryRef,0.0);
            AnchorPane.setLeftAnchor(entireInventoryRef,0.0);
            AnchorPane.setBottomAnchor(entireInventoryRef,0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
