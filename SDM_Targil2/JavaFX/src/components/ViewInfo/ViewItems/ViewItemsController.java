package components.ViewInfo.ViewItems;

import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Inventory.ePurchaseCategory;
import Logic.Order.StoreItem;
import Logic.SDM.SDMManager;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewItemsController {

    @FXML
    private TableView<ItemWrapper> itemsTableView;

    @FXML
    private TableColumn<ItemWrapper, Integer> itemIdColumn;

    @FXML
    private TableColumn<ItemWrapper, String> itemNameColumn;

    @FXML
    private TableColumn<ItemWrapper, ObjectProperty<ePurchaseCategory>> itemCategoryColumn;

    @FXML
    private TableColumn<ItemWrapper, Integer> numCarryingStoresColumn;

    @FXML
    private TableColumn<ItemWrapper, Float> avePriceColumn;

    @FXML
    private TableColumn<ItemWrapper, Float> totalAmountSoldColumn;


    private SDMManager sdmManager;
    private Inventory inventory;
    private ObservableList<ItemWrapper> itemWrappers = FXCollections.observableArrayList();

    //This inner class is used to fill tableView columns for totalSold, averagePrice, etc.
    public class ItemWrapper extends InventoryItem {
        private Float averagePrice;
        private float totalSold;
        private int numberCarryingStores;

        public ItemWrapper(InventoryItem item, float avePrice, float totalSold, int numberCarryingStores){
            super(item);
            this.averagePrice = avePrice;
            this.totalSold=totalSold;
            this.numberCarryingStores = numberCarryingStores;
        }

        public Float getAveragePrice() {
            return averagePrice;
        }

        public float getTotalSold() {
            return totalSold;
        }

        public int getNumberCarryingStores() {
            return numberCarryingStores;
        }
    }

    public ViewItemsController(){
        sdmManager = SDMManager.getInstance();
        inventory = sdmManager.getInventory();

        for (InventoryItem item: inventory.getListInventoryItems()){
            Float avePrice = inventory.getMapItemsToAvePrice().get(item);
            Float totalSold = inventory.getMapItemsToTotalSold().get(item);
            int numberCarryingStores = inventory.getMapItemsToStoresWithItem().get(item).size();
            System.out.printf("Creating item for item %d (%s), with ave price = %.2f, total sold = %.2f, and num carrying stores = %d\n",
                    item.getInventoryItemId(), item.getItemName(), avePrice, totalSold, numberCarryingStores);
            itemWrappers.add(new ItemWrapper(item, avePrice, totalSold,numberCarryingStores));
        }
    }

    public void initialize(){

        itemIdColumn.setCellValueFactory(new PropertyValueFactory<ItemWrapper,Integer>("inventoryItemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<ItemWrapper,String>("itemName"));
        itemCategoryColumn.setCellValueFactory(new PropertyValueFactory<ItemWrapper, ObjectProperty<ePurchaseCategory>>("purchaseCategory"));
        avePriceColumn.setCellValueFactory(new PropertyValueFactory<ItemWrapper, Float>("averagePrice"));
        totalAmountSoldColumn.setCellValueFactory(new PropertyValueFactory<ItemWrapper, Float>("totalSold"));
        numCarryingStoresColumn.setCellValueFactory(new PropertyValueFactory<ItemWrapper, Integer>("numberCarryingStores"));

        itemsTableView.setItems(itemWrappers);
    }


}
