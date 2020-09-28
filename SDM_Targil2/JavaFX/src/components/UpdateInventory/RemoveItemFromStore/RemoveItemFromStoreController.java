package components.UpdateInventory.RemoveItemFromStore;

import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Order.StoreItem;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import components.PlaceAnOrder.SuccessOrError.AlertInfoBox;
import components.PlaceAnOrder.SuccessOrError.SuccessPopUpController;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RemoveItemFromStoreController implements Initializable {

    @FXML
    private Label chooseItemValueLabel;
    @FXML
    private Accordion accordian;

    @FXML
    private TitledPane chooseStoresPane;

    @FXML
    private ComboBox<Store> chooseStoreCB;

    @FXML
    private Label selectedStoreLabel;

    @FXML
    private Button nextButton;


    @FXML
    private TitledPane chooseItemsPane;

    @FXML
    private ComboBox<StoreItem> chooseItemCB;

    @FXML
    private Button deleteButton;

    private Inventory inventory;
    private ObservableList<Store> stores;
    private javafx.beans.value.ChangeListener<Store> storeChangeListener;
    private Store selectedStore;

    private ObservableList<StoreItem> storeItems;
    private ChangeListener<StoreItem> storeItemChangeListener;
    private StoreItem selectedItem;

    public RemoveItemFromStoreController(){
        inventory = SDMManager.getInstance().getInventory();
        stores= FXCollections.observableArrayList(SDMManager.getInstance().getStores());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpFields();
        accordian.setExpandedPane(chooseStoresPane);
    }

    public void refresh(){
        chooseItemCB.getItems().clear();
        chooseStoreCB.getItems().clear();
        stores.clear();
        inventory = SDMManager.getInstance().getInventory();
        setUpFields();
    }

    private void setUpFields() {
        accordian.setExpandedPane(chooseStoresPane);
        chooseStoreCB.setItems(stores);

        chooseStoreCB.getSelectionModel().selectedItemProperty().addListener(
                storeChangeListener = (((observable, oldValue, newValue) -> {
                    selectedStore = newValue;
                    if (newValue != null){
                        chooseItemValueLabel.setText("Choose item to delete from Store " + selectedStore.getStoreName());
                        storeItems= FXCollections.observableArrayList(newValue.getStoreItems());
                        chooseItemCB.getItems().clear();
                        chooseItemCB.setItems(storeItems);

                        //combobox dropdown list shows item id and name
                        chooseItemCB.setCellFactory(new Callback<ListView<StoreItem>,ListCell<StoreItem>>(){
                            @Override
                            public ListCell<StoreItem> call(ListView<StoreItem> l){
                                return new ListCell<StoreItem>(){
                                    @Override
                                    protected void updateItem(StoreItem item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (item == null || empty) {
                                            setGraphic(null);
                                        } else {
                                            setText(item.getItemId() + " - " + item.getItemName());
                                        }
                                    }
                                };
                            }
                        });

                        //combobox selected value shows customer id and name
                        chooseItemCB.setConverter(new StringConverter<StoreItem>() {
                            @Override
                            public String toString(StoreItem item) {
                                if (item == null){
                                    return null;
                                } else {
                                    return item.getItemId() + " - " + item.getItemName();
                                }
                            }

                            @Override
                            public StoreItem fromString(String userId) {
                                return null;
                            }
                        });

                        chooseItemCB.getSelectionModel().selectFirst();
                    }
                }))
        );

        chooseItemCB.getSelectionModel().selectedItemProperty().addListener(
                storeItemChangeListener = ((observable, oldValue, newValue) -> {
                    selectedItem = newValue;
                    if (newValue!= null){

                    }
                })
        );
    }

    @FXML
    void deleteButtonAction(ActionEvent event) {
        if (selectedStore == null || selectedItem == null)
            return;

        if (selectedItem != null && selectedStore != null){
            if (selectedStore.getStoreItems().size()==1){
                new AlertInfoBox().display("Error", "Unable to perform this operation", "Currently item " + selectedItem.getItemId() + " is the only item sold at " + selectedStore.getStoreName() +".\nEach store must sell at least one item.");
                return;
            }

            boolean ans = SDMManager.getInstance().checkIfItemCanBeRemovedFromStore(selectedItem,selectedStore);
            if (!ans){
                new AlertInfoBox().display("Error", "Unable to perform this operation", "Currently item " + selectedItem.getItemId() + " is only sold at " + selectedStore.getStoreName() +".\nEach item must be sold in at least one store.");
                return;
            }
        }
        InventoryItem item = selectedStore.getInventoryItemById(selectedItem.getItemId());
        selectedStore.removeStoreItem(selectedItem);
        inventory.getMapItemsToStoresWithItem().get(item).remove(selectedStore);
        inventory.updateAvePrice();


        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/components/PlaceAnOrder/SuccessOrError/SuccessPopUp.fxml"));
            Parent root = null;
            root = loader.load();
            SuccessPopUpController controller = loader.getController();
            controller.setSuccess_error_label_Text("Item successfully removed!");

            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        chooseItemValueLabel.setText("");
        chooseItemCB.getItems().clear();

        selectedStore = null;
        chooseStoreCB.getSelectionModel().clearSelection();
        accordian.setExpandedPane(chooseStoresPane);
    }

    @FXML
    void nextButtonAction(ActionEvent event) {
        accordian.setExpandedPane(chooseItemsPane);
    }

}
