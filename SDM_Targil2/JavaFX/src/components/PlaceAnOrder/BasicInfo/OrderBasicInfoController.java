package components.PlaceAnOrder.BasicInfo;


import Logic.Customers.Customer;
import Logic.Order.eOrderType;
import Logic.SDM.SDMManager;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.LocalDate;

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
        customers = FXCollections.observableArrayList(sdmManager.getCustomers().getListOfCustomers());
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
        customers = FXCollections.observableArrayList(sdmManager.getCustomers().getListOfCustomers());
        setUpFields();
    }

    private void setUpFields(){

        chooseCustomerCB.getItems().addAll(customers);

        //combobox dropdown list shows customer id and name
        chooseCustomerCB.setCellFactory(new Callback<ListView<Customer>,ListCell<Customer>>(){
            @Override
            public ListCell<Customer> call(ListView<Customer> l){
                return new ListCell<Customer>(){
                    @Override
                    protected void updateItem(Customer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getCustomerId() + " - " + item.getCustomerName());
                        }
                    }
                } ;
            }
        });

        //combobox selected value shows customer id and name
        chooseCustomerCB.setConverter(new StringConverter<Customer>() {
            @Override
            public String toString(Customer customer) {
                if (customer == null){
                    return null;
                } else {
                    return customer.getCustomerId() + " - " + customer.getCustomerName();
                }
            }

            @Override
            public Customer fromString(String userId) {
                return null;
            }
        });

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
