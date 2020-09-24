package components.PlaceAnOrder.ConfirmOrder;

import Logic.Customers.Customer;
import Logic.Order.Cart;
import Logic.Order.CartItem;
import Logic.Order.eOrderType;
import Logic.Store.Store;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
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
    private TableColumn<CartItem, String> itemCostColumn;

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
    private ObservableMap<Store, ObservableList<CartItem>> mapStoreToCartItems;
    private ObjectProperty<Customer> customerObjectProperty;
    private ObjectProperty<LocalDate> localDateObjectProperty;
    private FloatProperty deliveryFee;

    public ConfirmOrderController(){
        cartItems = FXCollections.observableArrayList();
        mapStoreToCartItems = FXCollections.observableHashMap();
        customerObjectProperty = new SimpleObjectProperty<>();
        localDateObjectProperty = new SimpleObjectProperty<>();
        deliveryFee = new SimpleFloatProperty(0);
    }

    public void bindCustomer(ObjectProperty<Customer> outsideCustomer){
        this.customerObjectProperty.bind(outsideCustomer);
    }

    public void bindDate(ObjectProperty<LocalDate> outsideDate){
        this.localDateObjectProperty.bind(outsideDate);
    }

    public void bindDeliveryFee(FloatProperty outsideDeliveryFee){
        this.deliveryFee.bind(outsideDeliveryFee);
    }


    public void fillViewsForData(Customer customer, LocalDate date, eOrderType orderType, Set<Store> storeSet, Cart cart, float deliveryFee){
        customerNameValueLabel.setText(customer.getCustomerName());
        orderDateValueLabel.setText(date.toString());
        if (storeSet.size()==1){
            storesValueLabel.setText(storeSet.stream().findFirst().get().getStoreName());
        } else{
            storesValueLabel.setText("");
        }
        fillTableViewForCartStaticCart(cart);
        cartSubtotalValueLabel.setText(String.valueOf(cart.getCartTotalPrice()));
        deliveryFeeValueLabel.setText(String.valueOf(deliveryFee));
        totalValueLabel.setText(String.valueOf(cart.getCartTotalPrice()+deliveryFee));
    }

    public void fillViewsForData(Customer customer, LocalDate date, eOrderType orderType, Store selectedStore, Cart cart, float deliveryFee){
        customerNameValueLabel.setText(customer.getCustomerName());
        orderDateValueLabel.setText(date.toString());
        storesValueLabel.setText(selectedStore.getStoreName());

        fillTableViewForCartStaticCart(cart);
        cartSubtotalValueLabel.setText(String.valueOf(cart.getCartTotalPrice()));
        deliveryFeeValueLabel.setText(String.valueOf(deliveryFee));
        totalValueLabel.setText(String.valueOf(cart.getCartTotalPrice()+deliveryFee));
    }

    public void fillViewsForDynamicData(Customer customer, LocalDate orderDate, HashMap<Store, Cart> mapStoresToCarts, float deliveryFee) {

        customerNameValueLabel.setText(customer.getCustomerName());
        orderDateValueLabel.setText(orderDate.toString());
        storesValueLabel.setText("(Multiple Stores");
        addStoreColumn();
        fillCartTableForDynamicOrder(mapStoresToCarts);
    }

    private void addStoreColumn() {
        TableColumn<CartItem,String> storeTableColumn = new TableColumn<>("Store");
        orderItemsTableView.getColumns().add(storeTableColumn);
        storeTableColumn.setCellValueFactory(new PropertyValueFactory<CartItem,String>("storeBoughtFrom"));
    }

    private void fillTableViewForCartStaticCart(Cart cart) {
        cart.getCart().forEach((k,v)->{
            cartItems.add(v);
        });
        cart.getDiscountCart().forEach((k,v)->{
            cartItems.add(v);
        });

        orderItemsTableView.getItems().clear();
        orderItemsTableView.setItems(cartItems);
    }

    private void fillCartTableForDynamicOrder(HashMap<Store, Cart> mapStoresToCarts){
        orderItemsTableView.getItems().clear();

        mapStoresToCarts.forEach((store,cart)->{
            if (mapStoreToCartItems.get(store)==null){
                ObservableList<CartItem> cartForStore = FXCollections.observableArrayList();
                mapStoreToCartItems.put(store,cartForStore);
            }
            cart.getCart().values().forEach(item->{
                mapStoreToCartItems.get(store).add(item);
                orderItemsTableView.getItems().add(item);
            });
            cart.getDiscountCart().values().forEach(item->{
                mapStoreToCartItems.get(store).add(item);
                orderItemsTableView.getItems().add(item);
            });
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        discountCol.setCellValueFactory(new PropertyValueFactory<CartItem,String>("discountName"));
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Integer>("itemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<CartItem,String>("itemName"));
        itemUnitPriceColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Integer>("price"));
        itemAmountColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Double>("itemAmount"));
        itemCostColumn.setCellValueFactory(celldata->{
            CartItem item = celldata.getValue();
            String res = String.format("%.2f",item.getItemAmount()* item.getPrice());
            return new ReadOnlyStringWrapper(res);
        });
    }


    public void resetFields() {

    }
}
