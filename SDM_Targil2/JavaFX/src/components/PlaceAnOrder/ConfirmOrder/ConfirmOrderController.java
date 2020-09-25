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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;

//TODO: Fill in Order-Date and Stores Label

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
    private float deliveryFee;
    private double cartsSubtotal;
    private double orderTotal;

    public ConfirmOrderController(){
        cartItems = FXCollections.observableArrayList();
        mapStoreToCartItems = FXCollections.observableHashMap();
        customerObjectProperty = new SimpleObjectProperty<>();
        localDateObjectProperty = new SimpleObjectProperty<>();
    }



//    public void fillViews(Customer customer, HashMap<Store, Cart> mapStoresToCarts) {
//        customerNameValueLabel.setText(customer.getCustomerName());
//        deliveryFee = 0;
//        cartsSubtotal = 0;
//
//        if (mapStoresToCarts.keySet().size()>1){
//            storesValueLabel.setVisible(false);
//            addStoreColumn();
//            fillCartTableForDynamicOrder(mapStoresToCarts);
//
//            mapStoresToCarts.forEach((store,cart)->{
//                deliveryFee+=store.getDeliveryCost(customer.getLocation());
//                cartsSubtotal+=cart.getCartTotalPrice();
//            });
//
//        } else{
//            Store store = mapStoresToCarts.keySet().stream().findFirst().orElse(null);
//            if (store!=null){
//                fillTableViewForCartStaticCart(mapStoresToCarts.get(store));
//                storesValueLabel.setText(store.getStoreName());
//                deliveryFee = store.getDeliveryCost(customer.getLocation());
//                cartsSubtotal = mapStoresToCarts.get(store).getCartTotalPrice();
//            }
//        }
//        deliveryFeeValueLabel.setText(String.format(String.valueOf(deliveryFee), "%.2f"));
//        cartSubtotalValueLabel.setText(String.format(String.valueOf(cartsSubtotal), "%.2f"));
//        double total = deliveryFee + cartsSubtotal;
//        BigDecimal bd = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
//        double shortedVal = bd.doubleValue();
//        totalValueLabel.setText(String.valueOf(shortedVal));
//    }

    public void fillViews(Customer customer, HashMap<Store, Cart> mapStoresToCarts, LocalDate orderDate) {
        customerNameValueLabel.setText(customer.getCustomerName());
        orderDateValueLabel.setText(orderDate.toString());
        deliveryFee = 0;
        cartsSubtotal = 0;
        StringBuilder sb = new StringBuilder("");

        if (mapStoresToCarts.keySet().size()>1){
            storesValueLabel.setVisible(false);
            addStoreColumn();
        }

        fillTableWithOrderItems(mapStoresToCarts);
        orderItemsTableView.setItems(cartItems);
        //orderItemsTableView.refresh();

        mapStoresToCarts.forEach((store,cart)->{
            sb.append(store.getStoreName()).append(",");
            deliveryFee+=store.getDeliveryCost(customer.getLocation());
            cartsSubtotal+=cart.getCartTotalPrice();
        });

        storesValueLabel.setText(sb.toString());
        deliveryFeeValueLabel.setText(String.format(String.valueOf(deliveryFee), "%.2f"));
        cartSubtotalValueLabel.setText(String.format(String.valueOf(cartsSubtotal), "%.2f"));
        double total = deliveryFee + cartsSubtotal;
        BigDecimal bd = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
        double shortedVal = bd.doubleValue();
        totalValueLabel.setText(String.valueOf(shortedVal));
    }

    private void fillTableWithOrderItems(HashMap<Store, Cart> mapStoresToCarts) {
        cartItems.clear();
        for (Cart cart :mapStoresToCarts.values()){
            cart.getCart().values().forEach(item->cartItems.add(item));
            cart.getDiscountCart().values().forEach(item->cartItems.add(item));
        }
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

    public void setUpTables(){
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpTables();
    }


    public void resetFields() {
        mapStoreToCartItems.clear();
        customerObjectProperty = new SimpleObjectProperty<>();
        localDateObjectProperty = new SimpleObjectProperty<>();
        deliveryFee = 0;
        orderTotal = 0;
        cartsSubtotal = 0;
        cartItems.clear();
        deliveryFee = 0;
        cartsSubtotal = 0;
        orderTotal = 0;
        orderItemsTableView.getItems().clear();
        cartItems =FXCollections.observableArrayList();

        setUpTables();
    }

}
