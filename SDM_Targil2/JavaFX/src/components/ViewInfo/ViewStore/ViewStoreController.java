package components.ViewInfo.ViewStore;

import Logic.Inventory.ePurchaseCategory;
import Logic.Order.StoreItem;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import Logic.Store.StoreChangeListener;
import javafx.beans.property.ObjectProperty;
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
    private TableColumn<Store, LocalDate> orderDateColumn;

    @FXML
    private TableColumn<Store, String> orderIDColumn;

    @FXML
    private TableColumn<Store, Integer> numItemsInCartColumn;

    @FXML
    private TableColumn<Store, Float> cartSubtotalColumn;

    @FXML
    private TableColumn<Store, Float> deliveryFeeColumn;

    @FXML
    private TableColumn<Store, Float> orderTotalColumn;

    @FXML
    private ListView<Store> listview;

    private Store selectedStore;

    // Observable objects returned by extractor (applied to each list element) are listened for changes and
    // transformed into "update" change of ListChangeListener.
    private ObservableList<Store> observableStoresList;
    private ChangeListener<Store> storeChangeListener;

    public ViewStoreController(){
        observableStoresList = SDMManager.getInstance().getStoresObservableList();
        for (Store store: SDMManager.getInstance().getStores())
            store.addStoreChangeListener(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Inside StoreUIController initialize()");

        SDMManager.getInstance().fillSampleData(observableStoresList);

        // Use a sorted list; sort by lastname; then by firstname
        SortedList<Store> sortedList = new SortedList<>(observableStoresList);

        // sort by lastname first, then by firstname; ignore notes
        sortedList.setComparator((p1, p2) -> {
            if (p1.getStoreId() < p2.getStoreId())
                return -1;
            else
                return 1;
        });
        listview.setItems(sortedList);

        itemIDColumn.setCellValueFactory(new PropertyValueFactory<StoreItem,Integer>("inventoryItemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<StoreItem,String>("itemName"));
        itemCategoryColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, ObjectProperty<ePurchaseCategory>>("purchaseCategory"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, Integer>("normalPrice"));
        itemAmountSoldColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, Float>("totalAmountSoldAtStore"));


        listview.getSelectionModel().selectedItemProperty().addListener(
                storeChangeListener = (((observable, oldValue, newValue) -> {

                    System.out.println("Selected item: " + newValue);
                    //newValue can be null if nothing is selected
                    selectedStore = newValue;

                    if (newValue != null){
                        System.out.println("newVluae is " + newValue);
                        updateBasicStoreInfo(newValue);
//                        basicInfoTableView.setItems(getStore(newValue));
                        storeInventoryTableView.setItems(FXCollections.observableList(newValue.getStoreItems()));

                        //populate controls with selected store information

                        //storeIDColumn.set
                    }
                }))
        );

        listview.getSelectionModel().selectFirst();
    }

    private void updateBasicStoreInfo(Store newValue) {
        storeIdLabel.setText(String.valueOf(newValue.getStoreId()));
        storeNameLabel.setText(newValue.getStoreName());
        storeLocationLabel.setText(newValue.getStoreLocation().toString());
        storePPKLabel.setText(String.valueOf(newValue.getDeliveryPpk()));
        storeTotalDeliveryIncomeLabel.setText(String.valueOf(newValue.getTotalDeliveryIncome()));
    }

    private ObservableList<Store> getStore(Store selectedStore) {
        ObservableList<Store> currentStore = FXCollections.observableArrayList();
        currentStore.removeAll();
        currentStore.add(selectedStore);

        return currentStore;
    }

    @Override
    public void storeWasChanged(Store store) {
        System.out.println("Store " + store.getStoreName() + " was changed!");
    }
}

//https://openjfx.io/javadoc/11/javafx.controls/javafx/scene/control/TableView.html

//http://tutorials.jenkov.com/javafx/scrollpane.html