package components.PlaceAnOrder.ConfirmOrder;

import Logic.Customers.Customer;
import Logic.Order.Cart;
import Logic.Order.CartItem;
import Logic.Order.eOrderType;
import Logic.Store.Store;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class ConfirmOrderController implements Initializable {

    @FXML
    private TableView<CartItem> orderItemsTableView;

    @FXML
    private TableColumn<CartItem, String> discountCol;

    @FXML
    private TableColumn<CartItem, Integer> itemIdColumn;

    @FXML
    private TableColumn<CartItem, String> itemNameColumn;

    @FXML
    private TableColumn<CartItem, Integer> itemUnitPriceColumn;

    @FXML
    private TableColumn<CartItem, Double> itemAmountColumn;

    @FXML
    private TableColumn<CartItem, Double> itemCostColumn;

    @FXML
    private Label customerNameValueLabel;

    @FXML
    private Label orderDateValueLabel;

    @FXML
    private Label storesValueLabel;

    @FXML
    private Label cartSubtotalValueLabel;

    @FXML
    private Label deliveryFeeValueLabel;

    @FXML
    private Label totalValueLabel;

    @FXML
    void noButtonAction(ActionEvent event) {

    }

    @FXML
    void yesButtonAction(ActionEvent event) {

    }


    private ObservableList<CartItem> cartItems;


    public void fillViewsForData(Customer customer, LocalDate date, eOrderType orderType, Set<Store> storeSet, Cart cart, float deliveryFee){
        customerNameValueLabel.setText(customer.getCustomerName());
        orderDateValueLabel.setText(date.toString());
        if (storeSet.size()==1){
            storesValueLabel.setText(storeSet.stream().findFirst().get().getStoreName());
        } else{
            storesValueLabel.setText("");
        }
        fillTableViewForCart(cart);
        cartSubtotalValueLabel.setText(String.valueOf(cart.getCartTotalPrice()));
        deliveryFeeValueLabel.setText(String.valueOf(deliveryFee));
        totalValueLabel.setText(String.valueOf(cart.getCartTotalPrice()+deliveryFee));
    }

    private void fillTableViewForCart(Cart cart) {
        cartItems = FXCollections.observableArrayList();
        cart.getCart().forEach((k,v)->{
            cartItems.add(v);
        });
        cart.getDiscountCart().forEach((k,v)->{
            cartItems.add(v);
        });

        orderItemsTableView.getItems().clear();
        orderItemsTableView.setItems(cartItems);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        discountCol.setCellValueFactory(new PropertyValueFactory<CartItem,String>("discountName"));
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Integer>("itemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<CartItem,String>("itemName"));
        itemUnitPriceColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Integer>("price"));
        itemAmountColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Double>("itemAmount"));
    }
}
