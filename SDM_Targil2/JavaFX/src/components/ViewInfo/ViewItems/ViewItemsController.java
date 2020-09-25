package components.ViewInfo.ViewItems;

import Logic.Interfaces.inventoryChangeInterface;
import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Inventory.ePurchaseCategory;
import Logic.SDM.SDMManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewItemsController implements inventoryChangeInterface {

    @FXML
    private TableView<InventoryItem> itemsTableView;

    @FXML
    private TableColumn<InventoryItem, Integer> itemIdColumn;

    @FXML
    private TableColumn<InventoryItem, String> itemNameColumn;

    @FXML
    private TableColumn<InventoryItem, ObjectProperty<ePurchaseCategory>> itemCategoryColumn;

    @FXML
    private TableColumn<InventoryItem, String> numCarryingStoresColumn;

    @FXML
    private TableColumn<InventoryItem, String> avePriceColumn;

    @FXML
    private TableColumn<InventoryItem, String> totalAmountSoldColumn;


    private String TAG = "ViewItemsController";
    private SDMManager sdmManager;
    private Inventory inventory;
    private ObservableList<InventoryItem> items;

    @Override
    public void onInventoryChanged() {
        System.out.println("ViewItemsController: Refreshing itemsTableView");
        itemsTableView.refresh();
    }

    @Override
    public void onSalesMapChanged() {
        System.out.println(TAG + " - sales map changed!");
        items.clear();
        items = FXCollections.observableArrayList(inventory.getListInventoryItems());
        itemsTableView.setItems(items);
    }

    @Override
    public void onNumberCarryingStoresChanged(InventoryItem item) {
        System.out.println(TAG + " - number of carrying stores for item " + item.getItemName() + " changed!");
        items.clear();
        items = FXCollections.observableArrayList(inventory.getListInventoryItems());
        itemsTableView.setItems(items);
    }


    public ViewItemsController(){
        sdmManager = SDMManager.getInstance();
        inventory = sdmManager.getInventory();
        inventory.addListener(this);
        items = FXCollections.observableArrayList(inventory.getListInventoryItems());
    }

    public void initialize(){
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<InventoryItem,Integer>("itemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<InventoryItem,String>("itemName"));
        itemCategoryColumn.setCellValueFactory(new PropertyValueFactory<InventoryItem, ObjectProperty<ePurchaseCategory>>("purchaseCategory"));
        //setUpAvePriceCol();

        avePriceColumn.setCellValueFactory(celldata->{
            InventoryItem item = celldata.getValue();
            float avePrice = inventory.getAveragePriceForItem(item);
            return new ReadOnlyStringWrapper(String.valueOf(avePrice));
        });

        totalAmountSoldColumn.setCellValueFactory(celldata ->{
            InventoryItem item = celldata.getValue();
            double res = inventory.getTotalSoldForItem(item);
            return new ReadOnlyStringWrapper(String.valueOf(res));
        });

        numCarryingStoresColumn.setCellValueFactory(celldata->{
            InventoryItem item = celldata.getValue();
            int num = inventory.getMapItemsToStoresWithItem().get(item).size();
            return new ReadOnlyStringWrapper(String.valueOf(num));
        });

//        setUpTotalSoldCol();
//        setUpNumberCarryingStoresCol();

        itemsTableView.setItems(items);
    }
//
//    private void setUpNumberCarryingStoresCol() {
//        numCarryingStoresColumn.setCellFactory(col->{
//            TableCell<InventoryItem, Integer> cell = new TableCell<InventoryItem,Integer>(){
//                @Override
//                protected void updateItem(Integer item, boolean empty) {
//                    super.updateItem(item, empty);
//                    this.setText(null);
//                    if (!isEmpty()){
//                        int rowIndex = this.getTableRow().getIndex();
//                        InventoryItem inventoryItem = this.getTableView().getItems().get(rowIndex);
//                        int numberCarryingStores = inventory.getMapItemsToStoresWithItem().get(inventoryItem).size();
//                        this.setText(String.valueOf(numberCarryingStores));
//                    }
//                }
//            };
//            return cell;
//        });
//    }

//    private void setUpTotalSoldCol() {
//        totalAmountSoldColumn.setCellFactory(col->{
//            TableCell<InventoryItem, Float> cell = new TableCell<InventoryItem,Float>(){
//                @Override
//                protected void updateItem(Float item, boolean empty) {
//                    super.updateItem(item, empty);
//                    this.setText(null);
//                    if (!isEmpty()){
//                        int rowIndex = this.getTableRow().getIndex();
//                        InventoryItem inventoryItem = this.getTableView().getItems().get(rowIndex);
//                        Double totalAmountSold = inventory.getMapItemsToTotalSold().get(inventoryItem);
//                        this.setText(String.valueOf(totalAmountSold));
//                    }
//                }
//            };
//            return cell;
//        });
//    }


//    private void setUpAvePriceCol() {
//        avePriceColumn.setCellFactory(col->{
//            TableCell<InventoryItem, Float> cell = new TableCell<InventoryItem,Float>(){
//                @Override
//                protected void updateItem(Float item, boolean empty) {
//                    super.updateItem(item, empty);
//                    this.setText(null);
//                    if (!isEmpty()){
//                        int rowIndex = this.getTableRow().getIndex();
//                        InventoryItem inventoryItem = this.getTableView().getItems().get(rowIndex);
//                        float avePrice = inventory.getMapItemsToAvePrice().get(inventoryItem);
//                        this.setText(String.valueOf(avePrice));
//                    }
//                }
//            };
//            return cell;
//        });
//    }

}
