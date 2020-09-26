package components.ViewInfo.ViewStore;

import Logic.Inventory.ePurchaseCategory;
import Logic.Order.Order;
import Logic.Order.OrderId;
import Logic.Order.StoreItem;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import Logic.Store.StoreChangeListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ViewStoreController implements Initializable, StoreChangeListener {


    @FXML
    private Label storeIdLabel;

    @FXML
    private Label storeNameLabel;

    @FXML
    private Label storeLocationLabel;

    @FXML
    private Label storePPKLabel;

    @FXML
    private Label storeTotalDeliveryIncomeLabel;


    @FXML
    private TableView<Store> basicInfoTableView;

    @FXML
    private TableColumn<Store, Integer> storeIDColumn;

    @FXML
    private TableColumn<Store, String> storeNameColumn;

    @FXML
    private TableColumn<Store, List<Integer>> storeLocationColumn;

    @FXML
    private TableColumn<Store, Integer> storePPKColumn;

    @FXML
    private TableColumn<Store, Float> storeDeliveryIncomeColumn;

    @FXML
    private TableView<StoreItem> storeInventoryTableView;

    @FXML
    private TableColumn<StoreItem, Integer> itemIDColumn;

    @FXML
    private TableColumn<StoreItem, String> itemNameColumn;

    @FXML
    private TableColumn<StoreItem, ObjectProperty<ePurchaseCategory>> itemCategoryColumn;

    @FXML
    private TableColumn<StoreItem, Integer> itemPriceColumn;

    @FXML
    private TableColumn<StoreItem, Float> itemAmountSoldColumn;



    @FXML
    private TableView storeOrdersTableView;

    @FXML
    private TableColumn<Order, LocalDate> orderDateColumn;

    @FXML
    private TableColumn<Order, ObjectProperty<OrderId>> orderIDColumn;

    @FXML
    private TableColumn<Order, String> numItemsInCartColumn;

    @FXML
    private TableColumn<Order, String> cartSubtotalColumn;

    @FXML
    private TableColumn<Order, Float> deliveryFeeColumn;

    @FXML
    private TableColumn<Order, String> orderTotalColumn;

    @FXML
    private ListView<Store> listview;

    private Store selectedStore;

    // Observable objects returned by extractor (applied to each list element) are listened for changes and
    // transformed into "update" change of ListChangeListener.
    private ObservableList<Store> observableStoresList;
    private ChangeListener<Store> storeChangeListener;
    private ObservableList<Order> storeOrders;
    private String TAG = "ViewStoresController";

    public ViewStoreController(){
        //observableStoresList = FXCollections.observableList(SDMManager.getInstance().getStores());
        observableStoresList = FXCollections.observableArrayList();
        for (Store store: SDMManager.getInstance().getStores())
            store.addStoreChangeListener(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpFields();
    }

    public void setUpFields(){
        SDMManager.getInstance().fillSampleData(observableStoresList);
        SortedList<Store> sortedList = new SortedList<>(observableStoresList);

        // sort by store ID
        sortedList.setComparator((p1, p2) -> {
            if (p1.getStoreId() < p2.getStoreId())
                return -1;
            else
                return 1;
        });
        listview.setItems(sortedList);

        //Set columns for TableView
        itemIDColumn.setCellValueFactory(new PropertyValueFactory<StoreItem,Integer>("itemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<StoreItem,String>("itemName"));
        itemCategoryColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, ObjectProperty<ePurchaseCategory>>("purchaseCategory"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, Integer>("normalPrice"));
        itemAmountSoldColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, Float>("totalAmountSoldAtStore"));

        listview.getSelectionModel().selectedItemProperty().addListener(
                storeChangeListener = (((observable, oldValue, newValue) -> {
                    //newValue can be null if nothing is selected
                    selectedStore = newValue;

                    if (newValue != null){
                        //System.out.println("Selected store: " + newValue);
                        updateBasicStoreInfo(newValue);
                        storeInventoryTableView.setItems(FXCollections.observableList(newValue.getStoreItems()));
                        storeOrders = FXCollections.observableArrayList(newValue.getStoreOrders());
                        storeOrdersTableView.getItems().clear();
                        storeOrdersTableView.setItems(storeOrders);
                    }
                }))
        );

        //select first store in list as default
        listview.getSelectionModel().selectFirst();

        orderIDColumn.setCellValueFactory(new PropertyValueFactory<Order,ObjectProperty<OrderId>>("orderId"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<Order,LocalDate>("orderDate"));
        deliveryFeeColumn.setCellValueFactory(new PropertyValueFactory<Order,Float>("totalDeliveryCost"));

        numItemsInCartColumn.setCellValueFactory(cellData->{
            Order order = cellData.getValue();
            String res = String.valueOf(order.getCartForThisOrder().getNumberOfTypesOfItemsInCart());
            return new ReadOnlyStringWrapper(res);
        });


        cartSubtotalColumn.setCellValueFactory(cellData->{
            Order order = cellData.getValue();
            String res = String.valueOf(order.getCartTotal());
            return new ReadOnlyStringWrapper(res);
        });

        orderTotalColumn.setCellValueFactory(cellData->{
            Order order = cellData.getValue();
            String res = String.valueOf(order.getCartTotal()+order.getTotalDeliveryCost());
            return new ReadOnlyStringWrapper(res);
        });
    }

    private void updateBasicStoreInfo(Store newValue) {
        storeIdLabel.setText(String.valueOf(newValue.getStoreId()));
        storeNameLabel.setText(newValue.getStoreName());
        storeLocationLabel.setText(newValue.getLocation().toString());
        storePPKLabel.setText(String.valueOf(newValue.getDeliveryPpk()));
        storeTotalDeliveryIncomeLabel.setText(String.valueOf(newValue.getTotalDeliveryIncome()));
    }

    @Override
    public void storeWasChanged(Store store) {
        System.out.println("Store " + store.getStoreName() + " was changed!");
        if (selectedStore == store){
            storeOrders = FXCollections.observableArrayList(selectedStore.getStoreOrders());
            storeOrdersTableView.getItems().clear();
            storeOrdersTableView.setItems(storeOrders);
            updateBasicStoreInfo(selectedStore);
            storeInventoryTableView.refresh();
        }
    }

    @Override
    public void orderWasAdded(Store store, Order order) {
        System.out.println(TAG + " - Store " + store.getStoreName() + " received a new Order!");
        if (selectedStore == store){
            storeOrders.add(order);
            storeOrdersTableView.refresh();
        }
        storeInventoryTableView.refresh();
    }

    @Override
    public void itemPriceChanged(Store store, StoreItem item) {
        System.out.println(TAG + " - Store " + store.getStoreName() + " sees price of item " + item.getItemName() + " changed!");
        if (selectedStore == store){
            storeInventoryTableView.setItems(FXCollections.observableList(store.getStoreItems()));
        }
    }

    @Override
    public void newStoreItemAdded(Store store, StoreItem item) {
        System.out.println(TAG + " - Store " + store.getStoreName() + " sees item " + item.getItemName() + " was added!");
        if (selectedStore == store){
            storeInventoryTableView.setItems(FXCollections.observableList(store.getStoreItems()));
        }
    }

    @Override
    public void storeItemWasDeleted(Store store, StoreItem item) {
        System.out.println(TAG + " - Store " + store.getStoreName() + " sees item " + item.getItemName() + " was removed!");
        if (selectedStore == store){
            storeInventoryTableView.setItems(FXCollections.observableList(store.getStoreItems()));
        }
    }

    public void refresh() {
        System.out.println(TAG + " - refresh()");
        observableStoresList.clear();
        observableStoresList = FXCollections.observableArrayList();
        for (Store store: SDMManager.getInstance().getStores())
            store.addStoreChangeListener(this);

        setUpFields();
    }
}

//https://openjfx.io/javadoc/11/javafx.controls/javafx/scene/control/TableView.html

//http://tutorials.jenkov.com/javafx/scrollpane.html