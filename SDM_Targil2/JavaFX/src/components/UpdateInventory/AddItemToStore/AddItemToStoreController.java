package components.UpdateInventory.AddItemToStore;

import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Order.StoreItem;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import components.PlaceAnOrder.SuccessOrError.SuccessPopUp;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AddItemToStoreController implements Initializable {

    @FXML
    private Accordion accordian;

    @FXML
    private TitledPane chooseStoreTitledPane;


    @FXML
    private Button nextFromChooseStoresButton;

    @FXML
    private Label selectedStoreLabel;

    @FXML
    private TitledPane chooseItemTitledPane;

    @FXML
    private Button backButton;

    @FXML
    private Button nextFromChooseItemsButton;

    @FXML
    private Label currentItemLabel;

    @FXML
    private TitledPane choosePriceTitledPane;

    @FXML
    private TextField priceTextField;

    @FXML
    private Button addButton;

    @FXML
    private Label errorLabel;

    @FXML
    void backButtonAction(ActionEvent event) {
        accordian.setExpandedPane(chooseStoreTitledPane);
    }

    @FXML
    void nextFromChooseItemsButtonAction(ActionEvent event) {
        if (selectedItem!=null)
            accordian.setExpandedPane(choosePriceTitledPane);
    }

    @FXML
    void nextFromChooseStoresButtonAction(ActionEvent event) {
        if (selectedStore != null)
            accordian.setExpandedPane(chooseItemTitledPane);
    }

    @FXML
    private ComboBox<Store> chooseStoreCB;

    @FXML
    private ComboBox<InventoryItem> chooseItemCB;


    @FXML
    void onAddButtonAction(ActionEvent event) {
        int val;
        if ((val=isTextAPositiveInt(priceTextField.getText().trim())) > 0){
            SDMManager.getInstance().addInventoryItemToStore(selectedItem,selectedStore,val);
            selectedStore.addItemToStoreInventory(selectedItem, val);
            errorLabel.setVisible(false);
            accordian.setExpandedPane(chooseStoreTitledPane);
            priceTextField.clear();
            new SuccessPopUp();
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

    private ObservableList<InventoryItem> itemsNotInSelectedStore;
    private ChangeListener<InventoryItem> itemChangeListener;
    private InventoryItem selectedItem;

    public AddItemToStoreController(){
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
        accordian.setExpandedPane(chooseStoreTitledPane);
        chooseStoreCB.setItems(stores);
        //chooseStoreCB.getSelectionModel().selectFirst();
        chooseStoreCB.getSelectionModel().selectedItemProperty().addListener(
                storeChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("Store change listener called!");
                    selectedStore = newValue;
                    if (newValue != null){
                        selectedStoreLabel.setText("Selected store" + selectedStore.getStoreName());
                        selectedStoreLabel.setVisible(true);
                        itemsNotInSelectedStore=FXCollections.observableArrayList(inventory.getListOfItemsNotSoldByStore(selectedStore));
                        chooseItemCB.getItems().clear();
                        chooseItemCB.setItems(itemsNotInSelectedStore);
                        chooseItemCB.getSelectionModel().selectFirst();
                    }
                }))
        );

        chooseItemCB.getSelectionModel().selectedItemProperty().addListener(
                itemChangeListener = ((observable, oldValue, newValue) -> {
                    System.out.println("Item Change listener called!");
                    selectedItem = newValue;
                    if (newValue!= null){
                    }
                })
        );
    }


}
