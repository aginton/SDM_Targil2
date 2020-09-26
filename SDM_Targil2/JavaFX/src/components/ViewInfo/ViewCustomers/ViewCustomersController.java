package components.ViewInfo.ViewCustomers;

import Logic.Customers.Customer;
import Logic.Customers.Customers;
import Logic.Interfaces.customersChangeInterface;
import Logic.SDM.SDMManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewCustomersController implements Initializable, customersChangeInterface {

    @FXML
    private TableView<Customer> customersTableView;

    @FXML
    private TableColumn<Customer, Integer> customerIdCol;

    @FXML
    private TableColumn<Customer, String> customerNameCol;

    @FXML
    private TableColumn<Customer, String> customerLocationCol;

    @FXML
    private TableColumn<Customer, String> customerNumOfOrdersCol;

    @FXML
    private TableColumn<Customer, Float> customerAveOrdersPriceCol;

    @FXML
    private TableColumn<Customer, Float> customerAveDeliveryPriceCol;

    private SDMManager sdmManager;
    private Customers allCustomers;
    private ObservableList<Customer> customersList;


    public ViewCustomersController() {

        sdmManager = SDMManager.getInstance();
        allCustomers = sdmManager.getCustomers();
        allCustomers.addListener(this);
        customersList = FXCollections.observableArrayList(allCustomers.getListOfCustomers());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setUpFields();
    }

    private void setUpFields() {

        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        customerLocationCol.setCellValueFactory(celldata -> {
            Customer customer = celldata.getValue();
            int xLoc = customer.getX();
            int yLoc = customer.getY();
            StringBuilder locString = new StringBuilder();
            locString.append("[").append(xLoc).append(",").append(yLoc).append("]");
            System.out.println("filling table for customer: " + customer.getCustomerName() + " on location: " + locString);
            return new ReadOnlyStringWrapper(locString.toString());
        });

        customerNumOfOrdersCol.setCellValueFactory(celldata -> {
            Customer customer = celldata.getValue();
            int numOfOrders = customer.getOrders().size();
            return new ReadOnlyStringWrapper(String.valueOf(numOfOrders));
        });

        customerAveOrdersPriceCol.setCellValueFactory(new PropertyValueFactory<>("aveOrdersPrice"));
        customerAveOrdersPriceCol.setCellFactory(c -> new TableCell<Customer, Float>() {
            @Override
            protected void updateItem(Float ave, boolean empty) {
                super.updateItem(ave, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", ave.floatValue()));
                }
            }
        });

        customerAveDeliveryPriceCol.setCellValueFactory(new PropertyValueFactory<>("aveDeliveryPrice"));
        customerAveDeliveryPriceCol.setCellFactory(c -> new TableCell<Customer, Float>() {
            @Override
            protected void updateItem(Float ave, boolean empty) {
                super.updateItem(ave, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", ave.floatValue()));
                }
            }
        });

        customersTableView.setItems(customersList);
    }

    @Override
    public void onCustomersChange() {

        System.out.println("ViewCustomersController: Refreshing itemsTableView");
        customersTableView.refresh();
    }

    public void refresh() {
        sdmManager = SDMManager.getInstance();
        allCustomers = sdmManager.getCustomers();
        allCustomers.addListener(this);
        customersList.clear();
        customersList = FXCollections.observableArrayList(allCustomers.getListOfCustomers());

        setUpFields();
    }
}