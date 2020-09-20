package components.ViewInfo.ViewMap;

import Logic.Customers.Customer;
import Logic.Store.Store;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class BasicInfoPopupController {
    @FXML
    private GridPane root;

    @FXML
    private Label idLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label ordersLabel;

    @FXML
    private Button okayButton;

    @FXML
    private Label idValueLabel;

    @FXML
    private Label nameValueLabel;

    @FXML
    private Label ordersValueLabel;

    @FXML
    private Label ppkLabel;

    @FXML
    private Label ppkValueLabel;

    @FXML
    void okayButtonAction(ActionEvent event) {
        Stage stage = (Stage)root.getScene().getWindow();
        stage.close();
    }

    public void setStoreInfo(Store store) {
        idLabel.setText("Store Id:");
        idValueLabel.setText(String.valueOf(store.getStoreId()));
        nameLabel.setText("Store name:");
        nameValueLabel.setText(store.getStoreName());
        ordersLabel.setText("Number of orders:");
        ordersValueLabel.setText(String.valueOf(store.getStoreOrders().size()));
        ppkLabel.setText("PPK:");
        ppkValueLabel.setText(String.valueOf(store.getDeliveryPpk()));
    }

    public void setCustomerInfo(Customer customer) {
        idLabel.setText("Store Id:");
        idValueLabel.setText(String.valueOf(customer.getCustomerId()));
        nameLabel.setText("Store name:");
        nameValueLabel.setText(customer.getCustomerName());
        ordersLabel.setText("Number of orders:");
        //ordersValueLabel.setText(String.valueOf(store.getStoreOrders().size()));
        ppkLabel.setVisible(false);
        ppkValueLabel.setVisible(false);
    }
}
