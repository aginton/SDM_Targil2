package components.PlaceAnOrder.ChooseItems.DynamicOrder;

import Logic.Order.Cart;
import Logic.Order.CartItem;
import Logic.Store.Store;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class DynamicSubOrderController implements Initializable {

    @FXML
    private Label storeIDValueLabel;

    @FXML
    private Label storeNameValueLabel;

    @FXML
    private Label storeLocationValueLabel;

    @FXML
    private Label storePPKValueLabel;

    @FXML
    private TableView<CartItem> itemTableView;

    @FXML
    private TableColumn<CartItem, Integer> itemIDColumn;

    @FXML
    private TableColumn<CartItem, String> itemNameColumn;

    @FXML
    private TableColumn<CartItem, Integer> itemPriceColumn;

    @FXML
    private TableColumn<CartItem, Double> itemAmountColumn;

    @FXML
    private TableColumn<CartItem, String> itemCostColumn;

    @FXML
    private Label numTypesOfItemsLabel;

    @FXML
    private Label cartSubtotalLabel;


    public void setData(Store store, Cart cart){
        storeIDValueLabel.setText(String.valueOf(store.getStoreId()));
        storeNameValueLabel.setText(store.getStoreName());
        storeLocationValueLabel.setText(String.valueOf(store.getLocation()));
        storePPKValueLabel.setText(String.valueOf(store.getDeliveryPpk()));
        numTypesOfItemsLabel.setText(String.valueOf(cart.getNumberOfTypesOfItemsInCart()));
        cartSubtotalLabel.setText(String.valueOf(cart.getCartTotalPrice()));

        ObservableList<CartItem> items = FXCollections.observableArrayList(cart.getCart().values());
        itemTableView.setItems(items);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemIDColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Integer>("itemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<CartItem,String>("itemName"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Integer>("price"));
        itemAmountColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Double>("itemAmount"));
        //itemCostColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Double>("cost"));

        itemCostColumn.setCellValueFactory(celldata->{
            CartItem item = celldata.getValue();
            double val = item.getItemAmount()*item.getPrice();
            return new ReadOnlyStringWrapper(String.valueOf(val));
        });
    }
}
