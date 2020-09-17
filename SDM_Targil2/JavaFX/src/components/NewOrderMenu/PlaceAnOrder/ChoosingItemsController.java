package components.NewOrderMenu.PlaceAnOrder;

import Logic.Inventory.InventoryItem;
import Logic.Inventory.ePurchaseCategory;
import Logic.Order.Cart;
import Logic.Order.CartItem;
import Logic.Store.Store;
import Utilities.MyFloatStringConverter;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
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
import java.util.ResourceBundle;

public class ChoosingItemsController implements Initializable {

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
    private TableColumn<CartItem, Float> amountColumn;

    @FXML
    private TableColumn<CartItem, Void> addButtonColumn;

    @FXML
    private TableColumn<CartItem, Void> removeButtonColumn;

    @FXML
    private Button addToCartButton;

    @FXML
    private TableView<?> cartTable;

    @FXML
    private Button cancelButton;

    @FXML
    private Button conformButton;

    private Store store;
    private ObservableList<CartItem> storeItems;
    private ChangeListener<CartItem> storeItemChangeListener;
    private FloatProperty selectedItemAmountProperty;
    private Cart currentCart;


    public ChoosingItemsController(){
        storeItems = FXCollections.observableArrayList();
        selectedItemAmountProperty = new SimpleFloatProperty(0);
        currentCart = new Cart();
    }


    public void initData(Store selectedStore) {
        store = selectedStore;
        for (InventoryItem item: selectedStore.getInventoryItems()){
            storeItems.add(new CartItem(item, 0,store.getMapItemToPrices().get(item.getInventoryItemId()), selectedStore));
        }
        itemsTableView.setItems(storeItems);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<CartItem, Integer>("inventoryItemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<CartItem,String>("itemName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<CartItem, ObjectProperty<ePurchaseCategory>>("purchaseCategory"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<CartItem, Integer>("price"));

        setUpAmountColumn();
        setUpAddButtonColumn();
        setUpRemoveButtonColumn();


//        itemsTableView.getSelectionModel().selectedItemProperty().addListener(
//                storeItemChangeListener = (((observable, oldValue, newValue) -> {
//                    selectedStoreItem = newValue;
//                    if (newValue != null){
//                    }
//                }))
//        );

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
                            float oldAmount = cartItem.getItemAmount();

                            cartItem.setItemAmount(oldAmount+1);

                            if (oldAmount == 0.0){
                                currentCart.add(cartItem);
                            }

                            System.out.println("Updated Cart: " + currentCart);
//                            cartItem.increaseAmount(1);
                            //itemsTableView.refresh();
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
                            float oldAmount = cartItem.getItemAmount();
                            if (oldAmount-1 <= 0){
                                cartItem.setItemAmount(0);
                                currentCart.removeItemFromCart(cartItem);
                            }
                            else{
                                cartItem.setItemAmount(oldAmount-1);
                            }
                            System.out.println("Updated Cart: " + currentCart);
                            //itemsTableView.refresh();
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
        amountColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Float>("itemAmount"));

        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MyFloatStringConverter()));

        amountColumn.setOnEditCommit(event -> {
            Float value = event.getOldValue();
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
        System.out.println("Updated Cart: " + currentCart);
    }

    private void updateAmountOfItemInCart(CartItem selectedItem, Float value) {
        if (!currentCart.getCart().containsKey(selectedItem.getInventoryItemId())){
            currentCart.add(selectedItem);
            System.out.println("Updated Cart: " + currentCart);
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


    @FXML
    void addToCartAction(ActionEvent event) {

    }

    @FXML
    void cancelAction(ActionEvent event) {

    }

    @FXML
    void confirmAction(ActionEvent event) {

    }

}
