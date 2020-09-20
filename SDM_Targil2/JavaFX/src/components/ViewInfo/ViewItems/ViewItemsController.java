package components.ViewInfo.ViewItems;

import Logic.Interfaces.inventoryChangeInterface;
import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Inventory.ePurchaseCategory;
import Logic.Order.CartItem;
import Logic.Order.StoreItem;
import Logic.SDM.SDMManager;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private TableColumn<InventoryItem, Integer> numCarryingStoresColumn;

    @FXML
    private TableColumn<InventoryItem, Float> avePriceColumn;

    @FXML
    private TableColumn<InventoryItem, Float> totalAmountSoldColumn;


    private SDMManager sdmManager;
    private Inventory inventory;
    private ObservableList<InventoryItem> items;

    @Override
    public void onInventoryChanged() {
        System.out.println("ViewItemsController: Refreshing itemsTableView");
        itemsTableView.refresh();
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
        setUpAvePriceCol();
        setUpTotalSoldCol();
        setUpNumberCarryingStoresCol();

        itemsTableView.setItems(items);
    }

    private void setUpNumberCarryingStoresCol() {
        numCarryingStoresColumn.setCellFactory(col->{
            TableCell<InventoryItem, Integer> cell = new TableCell<InventoryItem,Integer>(){
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!isEmpty()){
                        int rowIndex = this.getTableRow().getIndex();
                        InventoryItem inventoryItem = this.getTableView().getItems().get(rowIndex);
                        int numberCarryingStores = inventory.getMapItemsToStoresWithItem().get(inventoryItem).size();
                        this.setText(String.valueOf(numberCarryingStores));
                    }
                }
            };
            return cell;
        });
    }

    private void setUpTotalSoldCol() {
        totalAmountSoldColumn.setCellFactory(col->{
            TableCell<InventoryItem, Float> cell = new TableCell<InventoryItem,Float>(){
                @Override
                protected void updateItem(Float item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!isEmpty()){
                        int rowIndex = this.getTableRow().getIndex();
                        InventoryItem inventoryItem = this.getTableView().getItems().get(rowIndex);
                        float totalAmountSold = inventory.getMapItemsToTotalSold().get(inventoryItem);
                        this.setText(String.valueOf(totalAmountSold));
                    }
                }
            };
            return cell;
        });
    }


    private void setUpAvePriceCol() {
        avePriceColumn.setCellFactory(col->{
            TableCell<InventoryItem, Float> cell = new TableCell<InventoryItem,Float>(){
                @Override
                protected void updateItem(Float item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    if (!isEmpty()){
                        int rowIndex = this.getTableRow().getIndex();
                        InventoryItem inventoryItem = this.getTableView().getItems().get(rowIndex);
                        float avePrice = inventory.getMapItemsToAvePrice().get(inventoryItem);
                        this.setText(String.valueOf(avePrice));
                    }
                }
            };
            return cell;
        });

//        avePriceColumn.setCellFactory(col->{
//            TableCell<CartItem, Void> cell = new TableCell<CartItem,Void>(){
//                @Override
//                protected void updateItem(Void item, boolean empty) {
//                    super.updateItem(item, empty);
//
//                    //Clean up cell before populating it
//                    this.setText(null);
//                    this.setGraphic(null);
//                    if (!empty){
//                        Button addButton = new Button("add");
//                        addButton.setOnAction(e->{
//                            int rowIndex = this.getTableRow().getIndex();
//                            CartItem cartItem = this.getTableView().getItems().get(rowIndex);
//                            float oldAmount = cartItem.getItemAmount();
//
//                            cartItem.setItemAmount(oldAmount+1);
//
//                            if (oldAmount == 0.0){
//                                currentCart.add(cartItem);
//                            }
//
//                            System.out.println("Updated Cart: " + currentCart);
////                            cartItem.increaseAmount(1);
//                            //itemsTableView.refresh();
//                        });
//                        this.setGraphic(addButton);
//                    }
//                }
//            };
//            return cell;
//        });
    }

}
