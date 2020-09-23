package components.PlaceAnOrder.ChooseItems.DynamicOrder;

import Logic.Inventory.InventoryItem;
import Logic.Inventory.ePurchaseCategory;
import Logic.Order.Cart;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import Utilities.MyDoubleStringConverter;
import components.ViewInfo.ViewOrders.SingleOrder.SingleOrderViewController;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;

public class ChooseItemsDynamicOrderController implements Initializable {

    @FXML
    private Label customerLabel;

    @FXML
    private Label customerLocationLabel;

    @FXML
    private TableView<InventoryItemWrapper> itemsTableView;

    @FXML
    private TableColumn<InventoryItemWrapper, Integer> itemIdColumn;

    @FXML
    private TableColumn<InventoryItemWrapper, String> itemNameColumn;

    @FXML
    private TableColumn<InventoryItemWrapper, ObjectProperty<ePurchaseCategory>> categoryColumn;

    @FXML
    private TableColumn<InventoryItemWrapper, Double> amountColumn;

    @FXML
    private TableColumn<InventoryItemWrapper, Void> addButtonColumn;

    @FXML
    private TableColumn<InventoryItemWrapper, Void> removeButtonColumn;


    @FXML
    private Button addToCartButton;

    @FXML
    private Label cartSubtotalLabel;

    @FXML
    private Label deliveryFeeLabel;

    @FXML
    private Label totalCostLabel;

    @FXML
    private FlowPane flowpane;

    private HashMap<InventoryItem, Double> mapItemsChosenToAmount;
    private ObservableList<InventoryItemWrapper> itemWrappers;
    private Cart cart;
    private Set<Store> storesBoughtFrom;

    public ChooseItemsDynamicOrderController(){
        mapItemsChosenToAmount = new HashMap<>();
        itemWrappers = FXCollections.observableArrayList();


        SDMManager.getInstance().getInventory().getListInventoryItems().forEach(item->{
            itemWrappers.add(new InventoryItemWrapper(item));
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<InventoryItemWrapper,Integer>("ItemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<InventoryItemWrapper,String>("ItemName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<InventoryItemWrapper, ObjectProperty<ePurchaseCategory>>("PurchaseCategory"));

        setUpAmountColumn();
        setUpAddButtonColumn();
        setUpRemoveButtonColumn();
        setTableEditable();
        itemsTableView.setItems(itemWrappers);
    }

    @FXML
    void addToCartAction(ActionEvent event) {
        cart = SDMManager.getInstance().findCheapestCartForUser(mapItemsChosenToAmount);
        storesBoughtFrom = cart.getStoresBoughtFrom();
        updateFlowPane();
    }

    private void updateFlowPane() {
        HashMap<Store,Cart> mapCartsToStore = new HashMap<>();
        storesBoughtFrom.forEach(s->{
            mapCartsToStore.put(s,new Cart());
        });

        cart.getCart().forEach((k,v)->{
            mapCartsToStore.get(v.getStoreBoughtFrom()).add(v);
        });

        mapCartsToStore.forEach((k,v)->{
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/PlaceAnOrder/ChooseItems/DynamicOrder/DynamicSubOrder.fxml"));
                Node n = loader.load();
                DynamicSubOrderController controller = loader.getController();
                controller.setData(v);
                flowpane.getChildren().add(n);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setUpAmountColumn() {
        amountColumn.setCellValueFactory(new PropertyValueFactory<InventoryItemWrapper, Double>("amount"));

        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new MyDoubleStringConverter()));

        amountColumn.setOnEditCommit(event -> {
            Double value = event.getOldValue();
            InventoryItemWrapper selectedItem = ((InventoryItemWrapper) event.getTableView().getItems().get(event.getTablePosition().getRow()));
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
                    selectedItem.setAmount(value);
                    if (value > 0){
                        addItemToMap(selectedItem);
                    }
                    else{
                        removeSelectedItemIfNecessary(selectedItem);
                    }
                }
            }
            itemsTableView.refresh();
        });
    }

    private void removeSelectedItemIfNecessary(InventoryItemWrapper selectedItem) {
        if (mapItemsChosenToAmount.containsKey((InventoryItem)selectedItem)){
            mapItemsChosenToAmount.remove((InventoryItem)selectedItem);
        }
    }

    private void addItemToMap(InventoryItemWrapper selectedItem) {
        InventoryItem item = SDMManager.getInstance().getInventory().getInventoryItemById(selectedItem.getItemId());
        if (mapItemsChosenToAmount.containsKey(item)){
            double amount = mapItemsChosenToAmount.get(item);
            amount += selectedItem.getAmount();
            mapItemsChosenToAmount.put(item, amount);
            return;
        } else{
            mapItemsChosenToAmount.put(item, selectedItem.getAmount());
        }
    }

    private void resestWrappers() {
        itemWrappers.forEach(i->{
            i.setAmount(0);
        });
        storesBoughtFrom.clear();
    }

    private void setUpAddButtonColumn() {
        addButtonColumn.setCellFactory(col->{
            TableCell<InventoryItemWrapper, Void> cell = new TableCell<InventoryItemWrapper,Void>(){
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
                            InventoryItemWrapper selectedItem = this.getTableView().getItems().get(rowIndex);
                            double oldAmount = selectedItem.getAmount();
                            selectedItem.setAmount(oldAmount+1);
                            addItemToMap(selectedItem);
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
            TableCell<InventoryItemWrapper, Void> cell = new TableCell<InventoryItemWrapper,Void>(){
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
                            InventoryItemWrapper selectedItem = this.getTableView().getItems().get(rowIndex);
                            double oldAmount = selectedItem.getAmount();
                            if (oldAmount-1 <= 0){
                                selectedItem.setAmount(0);
                                removeSelectedItemIfNecessary(selectedItem);
                            }
                            else{
                                selectedItem.setAmount(oldAmount-1);
                            }
                        });
                        this.setGraphic(removeButton);
                    }
                }
            };
            return cell;
        });
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
        final TablePosition<InventoryItemWrapper, ?> focusedCell = itemsTableView
                .focusModelProperty().get().focusedCellProperty().get();
        itemsTableView.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }

    public Cart getCart() {
        return cart;
    }

    public Set<Store> getStoresBoughtFrom() {
        return storesBoughtFrom;
    }


    public class InventoryItemWrapper extends InventoryItem{
        private DoubleProperty amount;

        public InventoryItemWrapper(InventoryItem item) {
            super(item);
            amount = new SimpleDoubleProperty(this, "amount",0);
        }

        public double getAmount() {
            return amount.get();
        }

        public DoubleProperty amountProperty() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount.set(amount);
        }
    }
}
