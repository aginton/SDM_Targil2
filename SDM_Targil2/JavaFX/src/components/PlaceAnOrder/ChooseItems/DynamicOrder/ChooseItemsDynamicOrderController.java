package components.PlaceAnOrder.ChooseItems.DynamicOrder;

import Logic.Customers.Customer;
import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Inventory.ePurchaseCategory;
import Logic.Order.Cart;
import Logic.Order.CartItem;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import Utilities.MyDoubleStringConverter;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
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
import java.util.List;
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

    private String TAG = "ChooseItemsDynamicOrderController";
    private Inventory inventory = SDMManager.getInstance().getInventory();
    private HashMap<InventoryItem, Double> mapItemsChosenToAmount;
    private ObservableList<InventoryItemWrapper> itemWrappers;
    //private ObservableMap<Store, List<CartItem>> mapStoreToCartItems;
//    private Cart cart;
//    private Set<Store> storesBoughtFrom;
    private HashMap<Store,Cart> mapStoresToCarts;
    private DoubleProperty cartsSubtotal;
    private FloatProperty deliveryFeeTotal;
    private Customer customer;
    private DoubleProperty regularItemsSubtotal;
    private BooleanProperty orderInProgress;


    public ChooseItemsDynamicOrderController(){
        mapItemsChosenToAmount = new HashMap<>();
        orderInProgress = new SimpleBooleanProperty(false);
        mapStoresToCarts = new HashMap<>();
//        mapStoreToCartItems=FXCollections.observableHashMap();
        itemWrappers = FXCollections.observableArrayList();
        cartsSubtotal = new SimpleDoubleProperty(0);
        deliveryFeeTotal = new SimpleFloatProperty(0f);
        regularItemsSubtotal = new SimpleDoubleProperty(0);
        SDMManager.getInstance().getInventory().getListInventoryItems().forEach(item->{
            itemWrappers.add(new InventoryItemWrapper(item));
        });

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deliveryFeeLabel.setText("???");
        deliveryFeeLabel.textProperty().bind(deliveryFeeTotal.asString("%.2f"));
        cartSubtotalLabel.textProperty().bind(cartsSubtotal.add(regularItemsSubtotal).asString("%.2f"));
        totalCostLabel.textProperty().bind(deliveryFeeTotal.add(cartsSubtotal).asString("%.2f"));


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
        if (!isOrderInProgress())
            setOrderInProgress(true);

        HashMap<InventoryItem, Double> dummyMap = new HashMap<>();
        for (InventoryItemWrapper wrapper: itemsTableView.getItems()){
            if (wrapper.getAmount()>0){
                InventoryItem item = inventory.getInventoryItemById(wrapper.getItemId());
                dummyMap.put(item, wrapper.getAmount());
            }
        }

        //cart = SDMManager.getInstance().findCheapestCartForUser(mapItemsChosenToAmount);
        HashMap<Store,Cart> dummyMapStoreToCart = SDMManager.getInstance().findCheapestStoresForItems(dummyMap);
        dummyMapStoreToCart.forEach((k,v)->{
            if (mapStoresToCarts.get(k)==null){
                mapStoresToCarts.put(k,v);
            } else{
                mapStoresToCarts.get(k).addCartToCart(v);
            }
        });

        updateDeliveryFeeTotal();
        updateFlowPane();
        resetCells();
    }

    private void updateDeliveryFeeTotal() {
        if (customer == null){
            System.out.println("ChooseItemsDynamicOrder customer is null!");
            return;
        }
        setDeliveryFeeTotal(0);

        List<Integer> customerLocation = customer.getLocation();
        mapStoresToCarts.keySet().forEach(store -> {
             float total = getDeliveryFeeTotal();
             setDeliveryFeeTotal(store.getDeliveryCost(customerLocation)+total);
        });
    }



    private void resetCells() {
        for (InventoryItemWrapper wrapper: itemWrappers){
            wrapper.setAmount(0);
        }
    }

    private void updateFlowPane() {
        flowpane.getChildren().clear();
        setCartsSubtotal(0);

        mapStoresToCarts.forEach((k, v)->{
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/PlaceAnOrder/ChooseItems/DynamicOrder/DynamicSubOrder.fxml"));
                Node n = loader.load();
                DynamicSubOrderController controller = loader.getController();
                controller.setData(k,v);
                flowpane.getChildren().add(n);
                double oldTotal = cartsSubtotal.getValue();
                oldTotal+= v.getCartTotalPrice();
                setCartsSubtotal(oldTotal);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public double getCartsSubtotal() {
        return cartsSubtotal.get();
    }

    public DoubleProperty cartsSubtotalProperty() {
        return cartsSubtotal;
    }

    public void setCartsSubtotal(double cartsSubtotal) {
        this.cartsSubtotal.set(cartsSubtotal);
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
        if (mapItemsChosenToAmount.containsKey(inventory.getInventoryItemById(selectedItem.getItemId()))){
            mapItemsChosenToAmount.remove(inventory.getInventoryItemById(selectedItem.getItemId()));
        }
    }

    private void addItemToMap(InventoryItemWrapper selectedItem) {
        InventoryItem item = inventory.getInventoryItemById(selectedItem.getItemId());
        if (mapItemsChosenToAmount.containsKey(item)){
            double amount = mapItemsChosenToAmount.get(item);
            amount += selectedItem.getAmount();
            mapItemsChosenToAmount.put(item, amount);
            return;
        } else{
            mapItemsChosenToAmount.put(item, selectedItem.getAmount());
        }
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
                            //InventoryItem inventoryItem = inventory.getInventoryItemById(selectedItem.getItemId());
                            //mapItemsChosenToAmount.put(inventoryItem,selectedItem.getAmount());
                            //addItemToMap(selectedItem);
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

    public boolean isOrderInProgress() {
        return orderInProgress.get();
    }

    public BooleanProperty orderInProgressProperty() {
        return orderInProgress;
    }

    public void setOrderInProgress(boolean orderInProgress) {
        this.orderInProgress.set(orderInProgress);
    }

    public boolean hasNecessaryInformation(){
        if (mapStoresToCarts.size()==0){
            System.out.println(TAG + "mapStoresToCarts can't be empty!");
            return false;
        }
        return true;
    }

    public HashMap<Store, Cart> getMapStoresToCarts() {
        return mapStoresToCarts;
    }


    public float getDeliveryFeeTotal() {
        return deliveryFeeTotal.get();
    }

    public FloatProperty deliveryFeeTotalProperty() {
        return deliveryFeeTotal;
    }

    public void setDeliveryFeeTotal(float deliveryFeeTotal) {
        this.deliveryFeeTotal.set(deliveryFeeTotal);
    }

    public void resetFields() {
        mapItemsChosenToAmount.clear();
        setDeliveryFeeTotal(0);
        setOrderInProgress(false);
//        mapStoreToCartItems.clear();
        mapStoresToCarts.clear();
        cartsSubtotal.setValue(0);
        flowpane.getChildren().clear();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
