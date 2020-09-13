package components.newOrderMenu;

import Logic.Customers.Customer;
import Logic.Order.eOrderType;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    private TitledPane itemsPane;

    @FXML
    private TitledPane salesPane;

    @FXML
    private TitledPane confirmPane;

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

    @FXML
    void backBtnAction(ActionEvent event) {
        if (panes.get(openPaneNumber)==customerPane){
            setIsCustomerSelected(false);
        }
        if (panes.get(openPaneNumber)==datePane){
            setIsDateSelected(false);
        }
        if (panes.get(openPaneNumber)==orderTypePane){
            setIsOrderTypeSelected(false);
        }
        if (panes.get(openPaneNumber)==storePane){
            setIsStoreSelected(false);
        }
        if (panes.get(openPaneNumber)==itemsPane){
            setIsItemsSelected(false);
        }
        if (panes.get(openPaneNumber)==salesPane){
            setIsSalesSelected(false);
        }
        accordion.setExpandedPane(panes.get(--openPaneNumber));
    }

    @FXML
    void confirmButtonAction(ActionEvent event) {

    }

    private List<Label> labels;
    private ObservableList<Customer> customers;
    private eOrderType orderType;
    private IntegerProperty activeLabelID = new SimpleIntegerProperty(0);
    private List<TitledPane> panes;
    private int openPaneNumber;
    private int numberOfPanes;
    private ObjectProperty<TitledPane> openPane;
    private Store selectedStore;
    private BooleanProperty isItemsSelected, isSalesSelected, isCustomerSelected, isDateSelected, isStoreSelected, isOrderTypeSelected;

    public boolean isIsItemsSelected() {
        return isItemsSelected.get();
    }

    public BooleanProperty isItemsSelectedProperty() {
        return isItemsSelected;
    }

    public void setIsItemsSelected(boolean isItemsSelected) {
        this.isItemsSelected.set(isItemsSelected);
    }

    public boolean isIsSalesSelected() {
        return isSalesSelected.get();
    }

    public BooleanProperty isSalesSelectedProperty() {
        return isSalesSelected;
    }

    public void setIsSalesSelected(boolean isSalesSelected) {
        this.isSalesSelected.set(isSalesSelected);
    }

    @FXML
    void nextBtnAction(ActionEvent event) {
        if (panes.get(openPaneNumber)==customerPane){
            setIsCustomerSelected(true);
        }
        if (panes.get(openPaneNumber)==datePane){
            setIsDateSelected(true);
        }
        if (panes.get(openPaneNumber)==orderTypePane){
            setIsOrderTypeSelected(true);
        }
        if (panes.get(openPaneNumber)==storePane){
            setIsStoreSelected(true);
        }
        if (panes.get(openPaneNumber)==itemsPane){
            setIsItemsSelected(true);
        }
        if (panes.get(openPaneNumber)==salesPane){
            setIsSalesSelected(true);
        }

        openPaneNumber++;
        accordion.setExpandedPane(panes.get(openPaneNumber));
    }



    public NewOrderContainerController(){
        sdmManager = SDMManager.getInstance();
        customers = FXCollections.observableArrayList(sdmManager.getCustomers().getCustomers());
        openPaneNumber = 0;

        isCustomerSelected = new SimpleBooleanProperty(this,"isCustomerSelected",false);
        isDateSelected = new SimpleBooleanProperty(this,"isDateSelected",false);
        isOrderTypeSelected = new SimpleBooleanProperty(this,"isOrderTypeSelected",false);
        isStoreSelected = new SimpleBooleanProperty(this,"isStoreSelected",false);
        isItemsSelected = new SimpleBooleanProperty(this,"isItemsSelected",false);
        isSalesSelected = new SimpleBooleanProperty(this,"isSalesSelected",false);

    }

    //https://stackoverflow.com/questions/51392203/what-does-initialize-mean-in-javafx
    @FXML
    private void initialize(){
        System.out.println("Inside OrderMenuController initialize() method!");
        chooseCustomerComboBox.getItems().addAll(customers);
        accordion.setExpandedPane(customerPane);
        panes = Arrays.asList(customerPane,datePane,orderTypePane,storePane,itemsPane,salesPane,confirmPane);
        numberOfPanes = panes.size();



        labels = new ArrayList<>();
        labels.add(chooseCustomerLabel);
        labels.add(chooseDateLabel);
        labels.add(chooseOrderTypeLabel);
        labels.add(chooseSalesLabel);
        labels.add(chooseSalesLabel);

        //nextButton.disableProperty().bind(activeLabelID.isEqualTo(labels.size()-1));
        backButton.disableProperty().bind(accordion.expandedPaneProperty().isEqualTo(customerPane));
        nextButton.disableProperty().bind(accordion.expandedPaneProperty().isEqualTo(confirmPane));

        confirmButton.disableProperty().bind(Bindings.and(isCustomerSelected,isDateSelected)
                .and(isOrderTypeSelected)
                .and(isStoreSelected)
                .and(isItemsSelected)
                .and(isSalesSelected).not());


//        backButton.disableProperty().bind(activeLabelID.isEqualTo(0));
//        confirmButton.disableProperty().bind(activeLabelID.isNotEqualTo(labels.size()-1));

        activeLabelID.addListener(((observable, oldValue, newValue) -> {
            System.out.println("activeLabelID changed: oldValue = " + oldValue + ", newValue = " + newValue);
        }));

        labels.get(activeLabelID.get()).getStyleClass().add("selected");
    }


    public boolean isIsCustomerSelected() {
        return isCustomerSelected.get();
    }

    public BooleanProperty isCustomerSelectedProperty() {
        return isCustomerSelected;
    }

    public void setIsCustomerSelected(boolean isCustomerSelected) {
        this.isCustomerSelected.set(isCustomerSelected);
    }

    public boolean isIsDateSelected() {
        return isDateSelected.get();
    }

    public BooleanProperty isDateSelectedProperty() {
        return isDateSelected;
    }

    public void setIsDateSelected(boolean isDateSelected) {
        this.isDateSelected.set(isDateSelected);
    }

    public boolean isIsStoreSelected() {
        return isStoreSelected.get();
    }

    public BooleanProperty isStoreSelectedProperty() {
        return isStoreSelected;
    }

    public void setIsStoreSelected(boolean isStoreSelected) {
        this.isStoreSelected.set(isStoreSelected);
    }

    public boolean isIsOrderTypeSelected() {
        return isOrderTypeSelected.get();
    }

    public BooleanProperty isOrderTypeSelectedProperty() {
        return isOrderTypeSelected;
    }

    public void setIsOrderTypeSelected(boolean isOrderTypeSelected) {
        this.isOrderTypeSelected.set(isOrderTypeSelected);
    }
}
