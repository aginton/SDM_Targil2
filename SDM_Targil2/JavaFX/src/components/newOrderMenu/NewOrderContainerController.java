package components.newOrderMenu;

import Logic.Customers.Customer;
import Logic.Order.eOrderType;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private List<Label> labels;
    private ObservableList<Customer> customers;
    private ChangeListener<Customer> customerChangeListener;
    private Customer selectedCustomer;
    private ChangeListener<LocalDate> orderDateChangeListener;
    private eOrderType orderType;
    private IntegerProperty activeLabelID = new SimpleIntegerProperty(0);
    private List<TitledPane> panes;
    private int openPaneNumber;
    private int numberOfPanes;
    private ObjectProperty<TitledPane> openPane;
    private ChangeListener<Toggle> orderTypeChangeListener;
    private Store selectedStore;
    private LocalDate orderDate;
    private ScrollPane entireInventoryRef;

    private final ObservableList<Store> storeList = FXCollections.observableArrayList(Store.extractor);
    private ChangeListener<Store> storeChangeListener;

    @FXML
    private void initialize(){
        System.out.println("Inside OrderMenuController initialize() method!");
        chooseCustomerComboBox.getItems().addAll(customers);
        accordion.setExpandedPane(customerPane);
        panes = Arrays.asList(customerPane,datePane,orderTypePane,storePane,itemsPane,salesPane,confirmPane);
        numberOfPanes = panes.size();
        for (int i=1; i<numberOfPanes; i++){
            panes.get(i).setDisable(true);
        }
        staticOrderRadioButton.setSelected(true);

        backButton.disableProperty().bind(accordion.expandedPaneProperty().isEqualTo(customerPane));
        nextButton.disableProperty().bind(accordion.expandedPaneProperty().isEqualTo(confirmPane));
        confirmButton.disableProperty().bind(accordion.expandedPaneProperty().isNotEqualTo(confirmPane));
        sdmManager.fillSampleData(storeList);
        storesListView.setItems(storeList);


        storesListView.getSelectionModel().selectedItemProperty().addListener(
                storeChangeListener = (((observable, oldValue, newValue) -> {

                    System.out.println("Selected store: " + newValue);
                    //newValue can be null if nothing is selected
                    selectedStore = newValue;

                    if (newValue != null){

                    }
                }))
        );


        chooseCustomerComboBox.getSelectionModel().selectedItemProperty().addListener(
                customerChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("Selected customer is " + newValue);
                    //newValue can be null if nothing is selected
                    selectedCustomer = newValue;

                    if (newValue != null){

                    }
                }))
        );

        chooseCustomerComboBox.getSelectionModel().selectFirst();

        orderTypeGroup.selectedToggleProperty().addListener(
                orderTypeChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("Order type: " + newValue);
                    if (newValue == staticOrderRadioButton){
                        orderType = eOrderType.STATIC_ORDER;
                    } else if (newValue == dynamicOrderRadioButton){
                        orderType = eOrderType.DYNAMIC_ORDER;
                    }
                }))
        );

        chooseDateDatePicker.valueProperty().addListener(
                orderDateChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("Selected date: " + newValue);
                    orderDate = newValue;
                }))
        );
    }



    @FXML
    void backBtnAction(ActionEvent event) {
        if (panes.get(openPaneNumber)==customerPane){
//            setIsCustomerSelected(false);
        }
        if (panes.get(openPaneNumber)==datePane){
//            setIsDateSelected(false);
        }
        if (panes.get(openPaneNumber)==orderTypePane){
//            setIsOrderTypeSelected(false);
        }
        if (panes.get(openPaneNumber)==storePane){
//            setIsStoreSelected(false);
        }
        if (panes.get(openPaneNumber)==itemsPane){
//            setIsItemsSelected(false);
        }
        if (panes.get(openPaneNumber)==salesPane){
//            setIsSalesSelected(false);
        }

        panes.get(openPaneNumber).setDisable(true);
        panes.get(--openPaneNumber).setDisable(false);
        accordion.setExpandedPane(panes.get(openPaneNumber));
    }

    @FXML
    void confirmButtonAction(ActionEvent event) {

    }




    @FXML
    void nextBtnAction(ActionEvent event) {


        if (panes.get(openPaneNumber)==orderTypePane){
            if (orderType==eOrderType.STATIC_ORDER){

            }
            else if (orderType == eOrderType.DYNAMIC_ORDER){
                panes.get(openPaneNumber).setDisable(true);
                panes.get(++openPaneNumber).setDisable(false);
                accordion.setExpandedPane(panes.get(openPaneNumber));
                loadAllInventoryItems();
            }
        }
        if (panes.get(openPaneNumber)==storePane){
            loadItemsForStore(selectedStore);
        }
        if (panes.get(openPaneNumber)==itemsPane){
        }
        if (panes.get(openPaneNumber)==salesPane){
        }

        panes.get(openPaneNumber).setDisable(true);
        panes.get(++openPaneNumber).setDisable(false);
        accordion.setExpandedPane(panes.get(openPaneNumber));
    }

    private void loadItemsForStore(Store selectedStore) {

    }

    private void loadAllInventoryItems() {
        try {
            System.out.println("Going to show entire inventory");
            entireInventoryRef = FXMLLoader.load(getClass().getResource("/components/newOrderMenu/ChooseItemsView/ChooseFromEntireInventory.fxml"));
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


    public NewOrderContainerController(){
        sdmManager = SDMManager.getInstance();
        customers = FXCollections.observableArrayList(sdmManager.getCustomers().getCustomers());
        openPaneNumber = 0;
    }

    //https://stackoverflow.com/questions/51392203/what-does-initialize-mean-in-javafx



//    public boolean isIsCustomerSelected() {
//        return isCustomerSelected.get();
//    }
//
//    public BooleanProperty isCustomerSelectedProperty() {
//        return isCustomerSelected;
//    }
//
//    public void setIsCustomerSelected(boolean isCustomerSelected) {
//        this.isCustomerSelected.set(isCustomerSelected);
//    }
//
//    public boolean isIsDateSelected() {
//        return isDateSelected.get();
//    }
//
//    public BooleanProperty isDateSelectedProperty() {
//        return isDateSelected;
//    }
//
//    public void setIsDateSelected(boolean isDateSelected) {
//        this.isDateSelected.set(isDateSelected);
//    }
//
//    public boolean isIsStoreSelected() {
//        return isStoreSelected.get();
//    }
//
//    public BooleanProperty isStoreSelectedProperty() {
//        return isStoreSelected;
//    }
//
//    public void setIsStoreSelected(boolean isStoreSelected) {
//        this.isStoreSelected.set(isStoreSelected);
//    }
//
//    public boolean isIsOrderTypeSelected() {
//        return isOrderTypeSelected.get();
//    }
//
//    public BooleanProperty isOrderTypeSelectedProperty() {
//        return isOrderTypeSelected;
//    }
//
//    public void setIsOrderTypeSelected(boolean isOrderTypeSelected) {
//        this.isOrderTypeSelected.set(isOrderTypeSelected);
//    }

    //    private BooleanProperty isItemsSelected, isSalesSelected, isCustomerSelected, isDateSelected, isStoreSelected, isOrderTypeSelected;
//
//    public boolean isIsItemsSelected() {
//        return isItemsSelected.get();
//    }
//
//    public BooleanProperty isItemsSelectedProperty() {
//        return isItemsSelected;
//    }
//
//    public void setIsItemsSelected(boolean isItemsSelected) {
//        this.isItemsSelected.set(isItemsSelected);
//    }
//
//    public boolean isIsSalesSelected() {
//        return isSalesSelected.get();
//    }
//
//    public BooleanProperty isSalesSelectedProperty() {
//        return isSalesSelected;
//    }
//
//    public void setIsSalesSelected(boolean isSalesSelected) {
//        this.isSalesSelected.set(isSalesSelected);
//    }
}
