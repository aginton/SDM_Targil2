package components.PlaceAnOrder.ChooseItems;

import Logic.Customers.Customer;
import Logic.Inventory.InventoryItem;
import Logic.Inventory.ePurchaseCategory;
import Logic.Order.Cart;
import Logic.Order.CartItem;
import Logic.Order.StoreItem;
import Logic.Store.Store;
import Utilities.MyDoubleStringConverter;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

//TODO: Update DeliveryFee and Total label after adding items
//TODO: Make cartSubtotal initially show regula items subtotal

public class ChooseItemsStaticOrderController implements Initializable{

    @FXML
    private Label customerLabel;

    @FXML
    private Label customerLocationLabel;

    @FXML
    private Label cartSubtotalLabel;

    @FXML
    private Label deliveryFeeLabel;

    @FXML
    private Label totalCostLabel;

    @FXML
    private TableView<ItemWrapper> itemsTableView;

    @FXML
    private TableColumn<ItemWrapper, Integer> itemIdColumn;

    @FXML
    private TableColumn<ItemWrapper, String> itemNameColumn;

    @FXML
    private TableColumn<ItemWrapper, ObjectProperty<ePurchaseCategory>> categoryColumn;

    @FXML
    private TableColumn<ItemWrapper, Integer> priceColumn;

    @FXML
    private TableColumn<ItemWrapper, Double> amountColumn;

    @FXML
    private TableColumn<ItemWrapper, Void> addButtonColumn;

    @FXML
    private TableColumn<ItemWrapper, Void> removeButtonColumn;

    @FXML
    private Button addToCartButton;

    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, Integer> cartItemIdCol;

    @FXML
    private TableColumn<CartItem, String> cartItemNameCol;

    @FXML
    private TableColumn<CartItem, Integer> cartUnitPriceCol;

    @FXML
    private TableColumn<CartItem, Double> cartItemAmountCol;

    @FXML
    private TableColumn<CartItem, Double> cartItemCostCol;

    private Store store;
    private ObservableList<ItemWrapper> storeItems;
    private DoubleProperty selectedItemAmountProperty;
    private HashMap<Integer, ItemWrapper> mapItemWrappersToAddToCart;
    private ObservableList<CartItem> cartItems;


    private ObjectProperty<Customer> customerObjectProperty;
    private DoubleProperty cartSubtotal;
    private FloatProperty deliveryFeeProperty;
    private DoubleProperty totalCost;
    private String TAG = "ChooseItemsStaticOrderController";
    private DoubleProperty regularItemsSubtotal;


    public ChooseItemsStaticOrderController(){
        mapItemWrappersToAddToCart = new HashMap<>();
        storeItems = FXCollections.observableArrayList();
        cartItems = FXCollections.observableArrayList();
        customerObjectProperty = new SimpleObjectProperty<>();
        regularItemsSubtotal = new SimpleDoubleProperty();

        cartSubtotal = new SimpleDoubleProperty(this, "cartSubtotal",0);

        deliveryFeeProperty= new SimpleFloatProperty(0);
        selectedItemAmountProperty = new SimpleDoubleProperty(0);
        totalCost = new SimpleDoubleProperty(0);
        totalCost.bind(cartSubtotal.add(deliveryFeeProperty));
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deliveryFeeLabel.textProperty().bind(deliveryFeeProperty.asString());
        totalCostLabel.textProperty().bind(totalCost.asString("%.2f"));
        cartSubtotalLabel.textProperty().bind(cartSubtotal.asString("%.2f"));
        setUpTableColumns();

        customerObjectProperty.addListener(((observable, oldValue, newValue) -> {
            System.out.println("ChooseItemsController customer change listener called!");
            if (newValue!=null)
                setDeliveryFeeProperty(store.getDeliveryCost(newValue.getLocation()));
        }));
    }

    public void setUpTableColumns(){

        itemIdColumn.setCellValueFactory(new PropertyValueFactory<ItemWrapper,Integer>("ItemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<ItemWrapper,String>("ItemName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<ItemWrapper, ObjectProperty<ePurchaseCategory>>("PurchaseCategory"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<ItemWrapper, Integer>("Price"));

        cartItemIdCol.setCellValueFactory(new PropertyValueFactory<CartItem,Integer>("itemId"));
        cartItemNameCol.setCellValueFactory(new PropertyValueFactory<CartItem,String>("itemName"));
        cartUnitPriceCol.setCellValueFactory(new PropertyValueFactory<CartItem,Integer>("price"));
        cartItemAmountCol.setCellValueFactory(new PropertyValueFactory<CartItem,Double>("itemAmount"));
        cartItemCostCol.setCellValueFactory(new PropertyValueFactory<CartItem,Double>("cost"));

        cartTable.setItems(cartItems);

        setUpAmountColumn();
        setUpAddButtonColumn();
        setUpRemoveButtonColumn();
        setTableEditable();
    }

    private void setUpAddButtonColumn() {
        addButtonColumn.setCellFactory(col->{
            TableCell<ItemWrapper, Void> cell = new TableCell<ItemWrapper,Void>(){
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    //Clean up cell before populating it
                    this.setText(null);
                    this.setGraphic(null);
                    if (!empty){
                        Button addButton = new Button("add");
                        addButton.setOnAction(e->{
                            int rowIndex = this.getTableRow().getIndex();
                            ItemWrapper itemWrapper = this.getTableView().getItems().get(rowIndex);
                            double oldAmount = itemWrapper.getItemAmount();
                            itemWrapper.setItemAmount(oldAmount+1);
                            if (!mapItemWrappersToAddToCart.containsKey(itemWrapper.getItemId())){
                                mapItemWrappersToAddToCart.put(itemWrapper.getItemId(), itemWrapper);
                            }
                        });
                        this.setGraphic(addButton);
                    }
                }
            };
            return cell;
        });
    }


    private void setUpRemoveButtonColumn() {
        removeButtonColumn.setCellFactory(col->{
            TableCell<ItemWrapper, Void> cell = new TableCell<ItemWrapper,Void>(){
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    //Clean up cell before populating it
                    this.setText(null);
                    this.setGraphic(null);
                    if (!empty){
                        Button removeButton = new Button("remove");
                        removeButton.setOnAction(e->{
                            int rowIndex = this.getTableRow().getIndex();
                            ItemWrapper itemWrapper = this.getTableView().getItems().get(rowIndex);
                            double oldAmount = itemWrapper.getItemAmount();
                            if (oldAmount-1 <= 0){
                                itemWrapper.setItemAmount(0);
                                removeSelectedItemIfNecessary(itemWrapper);
                            }
                            else{
                                itemWrapper.setItemAmount(oldAmount-1);
                            }
                        });
                        this.setGraphic(removeButton);
                    }
                }
            };
            return cell;
        });
    }


    private void setUpAmountColumn() {
        amountColumn.setCellValueFactory(new PropertyValueFactory<ItemWrapper, Double>("itemAmount"));

        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MyDoubleStringConverter()));

        amountColumn.setOnEditCommit(event -> {
            Double value = event.getOldValue();
            ItemWrapper selectedItem = ((ItemWrapper) event.getTableView().getItems().get(event.getTablePosition().getRow()));
            Boolean isValidNewAmount = true;

            if (event.getNewValue() != null){
                System.out.println("event.getNewValue() is not null");

                if ((event.getNewValue() - value == 0)){
                    System.out.println("event.getOldValue() is equal to event.getNewValue()");
                    isValidNewAmount = false;
                }

                if ((event.getNewValue() - value) != 0){
                    System.out.println("event.getOldValue() is not equal to event.getNewValue()");

                    if (selectedItem.getStoreItem().getPurchaseCategory() == ePurchaseCategory.QUANTITY){
                        if (event.getNewValue() != Math.round(event.getNewValue())){
                            isValidNewAmount = false;
                            System.out.println("Amount for this item must be a positive integer!");
                        }
                    }

                }
                if (isValidNewAmount){
                    value = event.getNewValue();
                    selectedItem.setItemAmount(value);
                    if (value > 0){
                        mapItemWrappersToAddToCart.put(selectedItem.getStoreItem().getItemId(), selectedItem);
                    }
                    else{
                        removeSelectedItemIfNecessary(selectedItem);
                    }
                }
            }
            itemsTableView.refresh();
        });
    }

    private void removeSelectedItemIfNecessary(ItemWrapper selectedItem) {
        if (mapItemWrappersToAddToCart.containsKey(selectedItem.getItemId())){
            mapItemWrappersToAddToCart.remove(selectedItem.getItemId());
        }
    }


    private void setTableEditable() {
        itemsTableView.setEditable(true);

        itemsTableView.getSelectionModel().cellSelectionEnabledProperty().set(true);

        // when character or numbers pressed it will start edit in editable
        // fields
        itemsTableView.setOnKeyPressed(event -> {
            if (event.getCode().isLetterKey() || event.getCode().isDigitKey())
            {
                editFocusedCell();
            } else if (event.getCode() == KeyCode.RIGHT
                    || event.getCode() == KeyCode.TAB) {
                itemsTableView.getSelectionModel().selectNext();
                event.consume();
            } else if (event.getCode() == KeyCode.LEFT) {
                event.consume();
            }
        });
    }

    private void editFocusedCell() {
        final TablePosition<ItemWrapper, ?> focusedCell = itemsTableView
                .focusModelProperty().get().focusedCellProperty().get();
        itemsTableView.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }

    public double getCartSubtotal() {
        return cartSubtotal.get();
    }

    public DoubleProperty cartSubtotalProperty() {
        return cartSubtotal;
    }

    public void setCartSubtotal(double cartSubtotal) {
        this.cartSubtotal.set(cartSubtotal);
    }

    @FXML
    void addToCartAction(ActionEvent event) {
        mapItemWrappersToAddToCart.forEach((k,v)->{

            double subtotal= cartSubtotal.doubleValue();
            subtotal += (v.getItemAmount()*v.getPrice());
            setCartSubtotal(subtotal);

            StoreItem item = v.getStoreItem();

            System.out.println("Adding itemId=" + item.getItemId() + ", itemAmount=" + v.getItemAmount());

            CartItem ITEM = cartItems.stream().filter(i->i.getItemId()==item.getItemId()).findFirst().orElse(null);

            if (ITEM!=null){
                double oldAmount = ITEM.getItemAmount();
                ITEM.setItemAmount(oldAmount+v.getItemAmount());
            } else{
                CartItem newCartItem = new CartItem((InventoryItem) item,
                        v.getItemAmount(),
                        v.getPrice(),v.getStoreBoughtFrom());

                cartItems.add(newCartItem);
            }

            v.setItemAmount(0);
            System.out.println("cartItems is now: " + cartItems);
        });
        mapItemWrappersToAddToCart.clear();
    }


    public Cart getCartForStaticOrder() {
        Cart dummyCart = new Cart();
        for (CartItem item: cartItems){
            dummyCart.add(item);
        }
        return dummyCart;
    }



    public void setDataForStaticOrder(Store selectedStore) {
        if (this.store != selectedStore){
            itemsTableView.getItems().clear();
            store = selectedStore;
            for (StoreItem item: selectedStore.getStoreItems()){
                storeItems.add(new ItemWrapper(item,selectedStore));
                //storeItems.add(new CartItem(item, 0,store.getMapItemToPrices().get(item.getItemId()), selectedStore));
            }
            itemsTableView.setItems(storeItems);
        }

        Customer c = customerObjectProperty.getValue();
        if (c!= null)
            setDeliveryFeeProperty(store.getDeliveryCost(c.getLocation()));
    }



    public void setDeliveryFeeValue(float val){
        setDeliveryFeeProperty(val);
    }

    public double getDeliveryFeeProperty() {
        return deliveryFeeProperty.get();
    }

    public FloatProperty deliveryFeePropertyProperty() {
        return deliveryFeeProperty;
    }

    public void setDeliveryFeeProperty(float deliveryFeeProperty) {
        this.deliveryFeeProperty.set(deliveryFeeProperty);
    }

    public Button getAddToCartButton() {
        return addToCartButton;
    }

    public void fillCustomerData(Customer customer) {
        customerLabel.setText("Customer: "+customer.getCustomerName());
        customerLocationLabel.setText("Customer Location: " + customer.getLocation());
    }

    public void setUpCustomerBinding(ObjectProperty<Customer> customerObjectProperty) {
        this.customerObjectProperty.bind(customerObjectProperty);
    }


    public void resetFields(){
        store = null;
        storeItems.clear();
        mapItemWrappersToAddToCart.clear();
        cartItems.clear();
        cartTable.getItems().clear();
        itemsTableView.getItems().clear();


        cartSubtotal.setValue(0);
        setCartSubtotal(0);
        setDeliveryFeeValue(0);

        storeItems = FXCollections.observableArrayList();
        cartItems = FXCollections.observableArrayList();
        setUpTableColumns();
    }

    public boolean hasNecessaryInformation(){
        if (cartItems.size() == 0){
            System.out.println(TAG + "cart can't be empty!");
            return false;
        }
        return true;
    }

    public HashMap<Store, Cart> getMapStoreToCart() {
        HashMap<Store,Cart> res = new HashMap<>();
        Cart cart = new Cart();
        cartItems.forEach(i->cart.add(i));
        res.put(this.store,cart);
        return res;
    }


    public class ItemWrapper {

        private StoreItem storeItem;
        private DoubleProperty itemAmount;
        private Store storeBoughtFrom;

        public ItemWrapper(StoreItem item, Store store){
            this.storeItem = item;
            itemAmount = new SimpleDoubleProperty(0);
            this.storeBoughtFrom=store;
        }

        public Store getStoreBoughtFrom() {
            return storeBoughtFrom;
        }

        public StoreItem getStoreItem() {
            return storeItem;
        }

        public double getItemAmount() {
            return itemAmount.get();
        }

        public DoubleProperty itemAmountProperty() {
            return itemAmount;
        }

        public void setItemAmount(double itemAmount) {
            this.itemAmount.set(itemAmount);
        }

        public int getItemId(){
            return storeItem.getItemId();
        }

        public String getItemName(){
            return storeItem.getItemName();
        }

        public int getPrice(){
            return storeItem.getNormalPrice();
        }
        public ePurchaseCategory getPurchaseCategory(){
            return storeItem.getPurchaseCategory();
        }

    }
}
