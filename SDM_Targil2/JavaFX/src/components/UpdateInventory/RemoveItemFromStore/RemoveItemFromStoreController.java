package components.UpdateInventory.RemoveItemFromStore;

import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Order.StoreItem;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;

import java.net.URL;
import java.util.ResourceBundle;

public class RemoveItemFromStoreController implements Initializable {
    @FXML
    private Accordion accordian;

    @FXML
    private TitledPane chooseStoresPane;

    @FXML
    private ComboBox<Store> chooseStoreCB;

    @FXML
    private TitledPane chooseItemsPane;

    @FXML
    private ComboBox<StoreItem> chooseItemCB;

    @FXML
    private Button deleteButton;

    private Inventory inventory = SDMManager.getInstance().getInventory();
    private ObservableList<Store> stores;
    private javafx.beans.value.ChangeListener<Store> storeChangeListener;
    private Store selectedStore;

    private ObservableList<StoreItem> storeItems;
    private ChangeListener<StoreItem> storeItemChangeListener;
    private StoreItem selectedItem;

    public RemoveItemFromStoreController(){
        stores= FXCollections.observableArrayList(SDMManager.getInstance().getStores());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accordian.setExpandedPane(chooseStoresPane);
        chooseStoreCB.setItems(stores);
        chooseStoreCB.getSelectionModel().selectFirst();
        chooseStoreCB.getSelectionModel().selectedItemProperty().addListener(
                storeChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("Store change listener called!");
                    selectedStore = newValue;
                    if (newValue != null){
                        storeItems= FXCollections.observableArrayList(newValue.getStoreItems());
                        chooseItemCB.getItems().clear();
                        chooseItemCB.setItems(storeItems);
                        chooseItemCB.getSelectionModel().selectFirst();
                        accordian.setExpandedPane(chooseItemsPane);
                    }
                }))
        );

        chooseItemCB.getSelectionModel().selectedItemProperty().addListener(
                storeItemChangeListener = ((observable, oldValue, newValue) -> {
                    System.out.println("Item Change listener called!");
                    selectedItem = newValue;
                    if (newValue!= null){

                    }
                })
        );
    }

    @FXML
    void deleteButtonAction(ActionEvent event) {
        InventoryItem item = selectedStore.getInventoryItemById(selectedItem.getItemId());
        selectedStore.removeStoreItem(selectedItem);
        inventory.getMapItemsToStoresWithItem().get(item).remove(selectedStore);
        inventory.updateAvePrice();
        accordian.setExpandedPane(chooseItemsPane);
    }

}
