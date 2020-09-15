package components.newOrderMenu.ChooseItemsView;

//https://stackoverflow.com/questions/30889732/javafx-tableview-change-row-color-based-on-column-value


import Logic.Inventory.InventoryItem;
import Logic.Inventory.ePurchaseCategory;
import Logic.Order.Cart;
import Logic.Order.CartItem;
import Logic.Order.StoreItem;
import Logic.Store.Store;
import Utilities.EditCell;
import Utilities.MyFloatStringConverter;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.converter.FloatStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class ChooseItemsController implements Initializable {
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

    private Store store;
    private ObservableList<CartItem> storeItems;
    private ChangeListener<CartItem> storeItemChangeListener;
    private CartItem selectedStoreItem;
    private FloatProperty selectedItemAmountProperty;
    private Cart currentCart;

    public ChooseItemsController(){
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


        itemsTableView.getSelectionModel().selectedItemProperty().addListener(
                storeItemChangeListener = (((observable, oldValue, newValue) -> {
                    selectedStoreItem = newValue;
//                    selectedItemAmountProperty = selectedStoreItem.pitemAmountProperty();
                    if (newValue != null){
                        System.out.println("aaaa");
                    }
                }))
        );

        setTableEditable();

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
        System.out.println("Updated Amount: " + currentCart);
    }

    private void updateAmountOfItemInCart(CartItem selectedItem, Float value) {
        if (!currentCart.getCart().containsKey(selectedItem.getInventoryItemId())){
            currentCart.add(selectedItem);
            System.out.println("Updated Amount: " + currentCart);
            return;
        }
        currentCart.updateItemAmount(selectedItem,value);
        System.out.println("Updated Amount: " + currentCart);
    }

//    private void setUpAmountColumn() {
//        amountColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Float>("itemAmount"));
//
//        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MyFloatStringConverter()));
//
//        amountColumn.setOnEditCommit(event -> {
//            Float value = event.getOldValue();
//            CartItem selectedItem = ((CartItem) event.getTableView().getItems().get(event.getTablePosition().getRow()));
//            Boolean isValidNewAmount = true;
//
//            if (event.getNewValue() != null){
//                System.out.println("event.getNewValue() is not null");
//
//                if ((event.getNewValue() - value == 0)){
//                    System.out.println("event.getOldValue() is equal to event.getNewValue()");
//                    isValidNewAmount = false;
//                }
//
//                if ((event.getNewValue() - value) != 0){
//                    System.out.println("event.getOldValue() is not equal to event.getNewValue()");
//
//                    if (selectedItem.getPurchaseCategory() == ePurchaseCategory.QUANTITY){
//                        if (event.getNewValue() != Math.round(event.getNewValue())){
//                            isValidNewAmount = false;
//                            System.out.println("Amount for this item must be a positive integer!");
//                        } else
//                            value = event.getNewValue();
//                    } else{
//                        value = event.getNewValue();
//                    }
//                }
//            }
//            selectedItem.setItemAmount(value);
//
//            //selectedItem.setPitemAmount(value);
//            itemsTableView.refresh();
//        });
//    }


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

}
