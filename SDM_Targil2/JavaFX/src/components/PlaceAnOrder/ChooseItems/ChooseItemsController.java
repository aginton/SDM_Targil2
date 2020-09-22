package components.PlaceAnOrder.ChooseItems;

import Logic.Inventory.InventoryItem;
import Logic.Inventory.ePurchaseCategory;
import Logic.Order.Cart;
import Logic.Order.CartItem;
import Logic.Order.eOrderType;
import Logic.Store.Store;
import Utilities.MyDoubleStringConverter;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class ChooseItemsController implements Initializable {

    @FXML
    private Label cartSubtotalLabel;

    @FXML
    private Label deliveryFeeLabel;

    @FXML
    private Label totalCostLabel;

    @FXML
    private TableView<CartItem> itemsTableView;

    @FXML
    private TableColumn<CartItem, Integer> itemIdColumn;

    @FXML
    private TableColumn<CartItem, String> itemNameColumn;

    @FXML
    private TableColumn<CartItem, ObjectProperty<ePurchaseCategory>> categoryColumn;

    @FXML
    private TableColumn<CartItem, Integer> priceColumn;

    @FXML
    private TableColumn<CartItem, Double> amountColumn;

    @FXML
    private TableColumn<CartItem, Void> addButtonColumn;

    @FXML
    private TableColumn<CartItem, Void> removeButtonColumn;

    @FXML
    private Button addToCartButton;

    @FXML
    private TableView<?> cartTable;

    private Store store;
    Set<Store> storeOfThisOrder = new HashSet<Store>();
    private ObservableList<CartItem> storeItems;
    private ChangeListener<CartItem> storeItemChangeListener;
    private DoubleProperty selectedItemAmountProperty;
    private Cart currentCart;

    private BooleanProperty isOrderComplete;
    private eOrderType orderType;
    private DoubleProperty deliveryFeeProperty;
    private DoubleProperty totalCost;


    public ChooseItemsController(){
        storeItems = FXCollections.observableArrayList();
        currentCart = new Cart();
        deliveryFeeProperty= new SimpleDoubleProperty(0);
        selectedItemAmountProperty = new SimpleDoubleProperty(0);
        totalCost = new SimpleDoubleProperty(0);
        totalCost.bind(deliveryFeeProperty.add(currentCart.cartTotalPriceProperty()));
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deliveryFeeLabel.textProperty().bind(deliveryFeeProperty.asString());
        cartSubtotalLabel.textProperty().bind(currentCart.cartTotalPriceProperty().asString());
        totalCostLabel.textProperty().bind(totalCost.asString());

        itemIdColumn.setCellValueFactory(new PropertyValueFactory<CartItem, Integer>("itemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<CartItem,String>("itemName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<CartItem, ObjectProperty<ePurchaseCategory>>("purchaseCategory"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<CartItem, Integer>("price"));

        setUpAmountColumn();
        setUpAddButtonColumn();
        setUpRemoveButtonColumn();
        setTableEditable();
    }

    private void setUpAddButtonColumn() {
        addButtonColumn.setCellFactory(col->{
            TableCell<CartItem, Void> cell = new TableCell<CartItem,Void>(){
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
                            CartItem cartItem = this.getTableView().getItems().get(rowIndex);
                            double oldAmount = cartItem.getItemAmount();

                            cartItem.setItemAmount(oldAmount+1);

                            if (oldAmount == 0.0){
                                currentCart.add(cartItem);
                            }

                            System.out.println("Updated Cart: " + currentCart);
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
            TableCell<CartItem, Void> cell = new TableCell<CartItem,Void>(){
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
                            CartItem cartItem = this.getTableView().getItems().get(rowIndex);
                            double oldAmount = cartItem.getItemAmount();
                            if (oldAmount-1 <= 0){
                                cartItem.setItemAmount(0);
                                currentCart.removeItemFromCart(cartItem);
                            }
                            else{
                                cartItem.setItemAmount(oldAmount-1);
                            }
                            System.out.println("Updated Cart: " + currentCart);
                        });
                        this.setGraphic(removeButton);
                    }
                }
            };
            return cell;
        });
    }

    public Cart getCurrentCart() {
        return currentCart;
    }

    private void setUpAmountColumn() {
        amountColumn.setCellValueFactory(new PropertyValueFactory<CartItem, Double>("itemAmount"));

        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MyDoubleStringConverter()));

        amountColumn.setOnEditCommit(event -> {
            Double value = event.getOldValue();
            CartItem selectedItem = ((CartItem) event.getTableView().getItems().get(event.getTablePosition().getRow()));
            Boolean isValidNewAmount = true;

            if (event.getNewValue() != null){
                System.out.println("event.getNewValue() is not null");

                if ((event.getNewValue() - value == 0)){
                    System.out.println("event.getOldValue() is equal to event.getNewValue()");
                    isValidNewAmount = false;
                }

                if ((event.getNewValue() - value) != 0){
                    System.out.println("event.getOldValue() is not equal to event.getNewValue()");

                    if (selectedItem.getPurchaseCategory() == ePurchaseCategory.QUANTITY){
                        if (event.getNewValue() != Math.round(event.getNewValue())){
                            isValidNewAmount = false;
                            System.out.println("Amount for this item must be a positive integer!");
                        }
                    }

                }
                if (isValidNewAmount){
                    value = event.getNewValue();
                    selectedItem.setItemAmount(value);
                    if (value > 0)
                        updateAmountOfItemInCart(selectedItem, value);
                    else
                        removeSelectedItemFromCart(selectedItem);
                }
            }
            itemsTableView.refresh();
        });
    }

    private void removeSelectedItemFromCart(CartItem selectedItem) {
        System.out.println("Calling removeSelectedItemFromCart()");
        currentCart.removeItemFromCart(selectedItem);
        //System.out.println("Updated Cart: " + currentCart);
    }

    private void updateAmountOfItemInCart(CartItem selectedItem, Double value) {
        if (!currentCart.getCart().containsKey(selectedItem.getItemId())){
            currentCart.add(selectedItem);
            System.out.println("Updated Cart:\n " + currentCart);
            return;
        }
        currentCart.updateItemAmount(selectedItem,value);
        // System.out.println("Updated Amount: " + currentCart);
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
        final TablePosition<CartItem, ?> focusedCell = itemsTableView
                .focusModelProperty().get().focusedCellProperty().get();
        itemsTableView.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }

    public void emptyCurrentCart(){
        currentCart = new Cart();
    }


    @FXML
    void addToCartAction(ActionEvent event) {

    }

//    @FXML
//    void confirmAction(ActionEvent event) {
//        System.out.println("Confirm button pressed");
//        ZoneId defaultZoneId = ZoneId.systemDefault();
//        Date date = Date.from(orderDate.atStartOfDay(defaultZoneId).toInstant());
//
//        if (orderType == eOrderType.STATIC_ORDER){
//            storeOfThisOrder.add(store);
//        }
//
//        Order order = new Order(customer.getLocation(),
//                date,
//                6,
//                currentCart,
//                storeOfThisOrder,
//                orderType);
//
//        SDMManager.getInstance().addNewStaticOrder(store, order);
//        setIsOrderComplete(true);
//    }

    public boolean getIsOrderComplete() {
        return isOrderComplete.get();
    }

    public BooleanProperty isOrderCompleteProperty() {
        return isOrderComplete;
    }

    public void setIsOrderComplete(boolean isOrderComplete) {
        this.isOrderComplete.set(isOrderComplete);
    }

    public void bindIsCompleteOrder(BooleanProperty otherBooleanProperty){
        isOrderComplete.bindBidirectional(otherBooleanProperty);
    }

    public void setDataForDynamicOrder() {

    }

    public void setDataForStaticOrder(Store selectedStore) {
        if (this.store != selectedStore){
            emptyCurrentCart();
            itemsTableView.getItems().clear();
            store = selectedStore;
            for (InventoryItem item: selectedStore.getInventoryItems()){
                storeItems.add(new CartItem(item, 0,store.getMapItemToPrices().get(item.getItemId()), selectedStore));
            }
            itemsTableView.setItems(storeItems);
        }
    }

    public void setDeliveryFeeValue(float val){
        setDeliveryFeeProperty(val);
    }

    public double getDeliveryFeeProperty() {
        return deliveryFeeProperty.get();
    }

    public DoubleProperty deliveryFeePropertyProperty() {
        return deliveryFeeProperty;
    }

    public void setDeliveryFeeProperty(float deliveryFeeProperty) {
        this.deliveryFeeProperty.set(deliveryFeeProperty);
    }

    public Button getAddToCartButton() {
        return addToCartButton;
    }
}
