package components.UpdateInventory.ChangeStoreItemPrice;

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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangeStoreItemPriceController implements Initializable {

    @FXML
    private Accordion accordian;

    @FXML
    private TitledPane storeTitledPane;

    @FXML
    private ComboBox<Store> chooseStoreCB;

    @FXML
    private TitledPane itemsTitledPane;

    @FXML
    private ComboBox<StoreItem> chooseItemCB;

    @FXML
    private TitledPane priceTitledPane;

    @FXML
    private TextField priceTextField;

    @FXML
    private Button addButton;

    @FXML
    private Label errorLabel;

    @FXML
    void onAddButtonAction(ActionEvent event) {
        int val;
        if ((val=isTextAPositiveInt(priceTextField.getText().trim())) > 0){
//            selectedItem.setNormalPrice(val);
//            selectedStore.getMapItemToPrices().put(selectedItem.getItemId(), val);
            selectedStore.updatePriceForItem(selectedItem,val);
            inventory.updateAvePrice();
            errorLabel.setVisible(false);
            accordian.setExpandedPane(storeTitledPane);
            priceTextField.clear();
            return;
        }
        errorLabel.setVisible(true);
    }

    private int isTextAPositiveInt(String text) {
        try{
            int val = Integer.parseInt(text);
            return val;
        } catch (Exception e){
            return -1;
        }
    }

    private Inventory inventory = SDMManager.getInstance().getInventory();
    private ObservableList<Store> stores;
    private ChangeListener<Store> storeChangeListener;
    private Store selectedStore;

    private ObservableList<StoreItem> storeItems;
    private ChangeListener<StoreItem> storeItemChangeListener;
    private StoreItem selectedItem;

    public ChangeStoreItemPriceController(){
        stores= FXCollections.observableArrayList(SDMManager.getInstance().getStores());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpFields();
    }

    public void refresh(){
        chooseStoreCB.getItems().clear();
        chooseItemCB.getItems().clear();
        stores.clear();
        stores= FXCollections.observableArrayList(SDMManager.getInstance().getStores());
        setUpFields();
    }

    private void setUpFields() {
        accordian.setExpandedPane(storeTitledPane);
        chooseStoreCB.setItems(stores);
        chooseStoreCB.getSelectionModel().selectFirst();
        chooseStoreCB.getSelectionModel().selectedItemProperty().addListener(
                storeChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("Store change listener called!");
                    selectedStore = newValue;
                    if (newValue != null){
                        storeItems= FXCollections.observableArrayList(selectedStore.getStoreItems());
                        chooseItemCB.getItems().clear();
                        chooseItemCB.setItems(storeItems);
                        chooseItemCB.getSelectionModel().selectFirst();
                        accordian.setExpandedPane(itemsTitledPane);
                    }
                }))
        );

        chooseItemCB.getSelectionModel().selectedItemProperty().addListener(
                storeItemChangeListener = ((observable, oldValue, newValue) -> {
                    System.out.println("Item Change listener called!");
                    selectedItem = newValue;
                    if (newValue!= null){
                        accordian.setExpandedPane(priceTitledPane);
                    }
                })
        );
    }
}
