package components.UpdateInventory.AddItemToStore;

import Logic.Customers.Customer;
import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Order.StoreItem;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import components.PlaceAnOrder.SuccessOrError.SuccessPopUp;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddItemToStoreController implements Initializable {
    @FXML
    private AnchorPane rootAnchorPane;


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
    private Label itemValueLabel;


    @FXML
    void nextFromChooseItemsButtonAction(ActionEvent event) {
        if (selectedItem!=null)
            accordian.setExpandedPane(choosePriceTitledPane);
    }

    @FXML
    void nextFromChooseStoresButton(ActionEvent event) {
        if (selectedStore != null)
            accordian.setExpandedPane(chooseItemTitledPane);
    }


    @FXML
    private ComboBox<Store> chooseStoreCB;

    @FXML
    private ComboBox<InventoryItem> chooseItemCB;

    @FXML
    private Label chooseItemValueLabel;

    @FXML
    private Label enterPriceValueLabel;


    @FXML
    void onAddButtonAction(ActionEvent event) {
        if (selectedStore == null || selectedItem == null)
            return;

        int val;
        if ((val=isTextAPositiveInt(priceTextField.getText().trim())) > 0){
            SDMManager.getInstance().addInventoryItemToStore(selectedItem,selectedStore,val);
            selectedStore.addItemToStoreInventory(selectedItem, val);
            errorLabel.setVisible(false);
            selectedStore = null;
            chooseStoreCB.getSelectionModel().clearSelection();
            priceTextField.clear();

            chooseItemValueLabel.setText("");
            chooseItemCB.getItems().clear();
            enterPriceValueLabel.setText("");


            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/components/PlaceAnOrder/SuccessOrError/SuccessPopUp.fxml"));
                Parent root = null;
                root = loader.load();
                SuccessPopUpController controller = loader.getController();
                controller.setSuccess_error_label_Text("Item successfully added!");

                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
            accordian.setExpandedPane(chooseStoreTitledPane);
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
        accordian.setExpandedPane(chooseStoreTitledPane);

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
                    selectedStore = newValue;
                    if (newValue != null){
                        chooseItemValueLabel.setText("Choose item to add to store " + selectedStore.getStoreName());
                        selectedStoreLabel.setText("Selected store" + selectedStore.getStoreName());
//                        selectedStoreLabel.setVisible(true);
                        itemsNotInSelectedStore=FXCollections.observableArrayList(inventory.getListOfItemsNotSoldByStore(selectedStore));
                        chooseItemCB.getItems().clear();
                        chooseItemCB.getItems().addAll(itemsNotInSelectedStore);

                        //combobox dropdown list shows item id and name
                        chooseItemCB.setCellFactory(new Callback<ListView<InventoryItem>,ListCell<InventoryItem>>(){
                            @Override
                            public ListCell<InventoryItem> call(ListView<InventoryItem> l){
                                return new ListCell<InventoryItem>(){
                                    @Override
                                    protected void updateItem(InventoryItem item, boolean empty) {
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

                        //combobox selected value shows item id and name
                        chooseItemCB.setConverter(new StringConverter<InventoryItem>() {
                            @Override
                            public String toString(InventoryItem item) {
                                if (item == null){
                                    return null;
                                } else {
                                    return item.getItemId() + " - " + item.getItemName();
                                }
                            }

                            @Override
                            public InventoryItem fromString(String userId) {
                                return null;
                            }
                        });

                        chooseItemCB.getSelectionModel().selectFirst();
                    }
                }))
        );

        chooseItemCB.getSelectionModel().selectedItemProperty().addListener(
                itemChangeListener = ((observable, oldValue, newValue) -> {
                    selectedItem = newValue;
                    if (newValue!= null){
                        enterPriceValueLabel.setText("Enter price for item " + selectedItem.getItemName() + " at store " + selectedStore.getStoreName());
                    }
                })
        );
    }


    public void bindToChildAnchorPane(AnchorPane childAnchorPane) {
        rootAnchorPane.prefWidthProperty().bind(childAnchorPane.widthProperty());
    }
}
