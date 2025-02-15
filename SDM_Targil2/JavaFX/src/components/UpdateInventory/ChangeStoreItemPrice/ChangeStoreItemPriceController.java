package components.UpdateInventory.ChangeStoreItemPrice;

import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Order.StoreItem;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
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

public class ChangeStoreItemPriceController implements Initializable {

    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private Accordion accordian;

    @FXML
    private TitledPane storeTitledPane;

    @FXML
    private ComboBox<Store> chooseStoreCB;

    @FXML
    private Label chooseItemValueLabel;

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
    private Label enterPriceValueLabel;

    @FXML
    void onAddButtonAction(ActionEvent event) {
        if (selectedStore == null || selectedItem == null)
            return;

        int val;
        if ((val=isTextAPositiveInt(priceTextField.getText().trim())) > 0){
//            selectedItem.setNormalPrice(val);
//            selectedStore.getMapItemToPrices().put(selectedItem.getItemId(), val);
            selectedStore.updatePriceForItem(selectedItem,val);
            inventory.updateAvePrice();
            errorLabel.setVisible(false);

            chooseItemValueLabel.setText("");
            chooseItemCB.getItems().clear();
            enterPriceValueLabel.setText("");

            priceTextField.clear();
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/components/PlaceAnOrder/SuccessOrError/SuccessPopUp.fxml"));
                Parent root = null;
                root = loader.load();
                SuccessPopUpController controller = loader.getController();
                controller.setSuccess_error_label_Text("Item price successfully updated!");

                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
            selectedStore = null;
            chooseStoreCB.getSelectionModel().clearSelection();
            accordian.setExpandedPane(storeTitledPane);
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
        accordian.setExpandedPane(storeTitledPane);
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

        chooseStoreCB.getSelectionModel().selectedItemProperty().addListener(
                storeChangeListener = (((observable, oldValue, newValue) -> {
                    selectedStore = newValue;
                    if (newValue != null){
                        chooseItemValueLabel.setText("Choose item whose price to change from store " + selectedStore.getStoreName());
                        storeItems= FXCollections.observableArrayList(selectedStore.getStoreItems());
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
                        enterPriceValueLabel.setText("Enter new price for item " + selectedItem.getItemName() + " at store " + selectedStore.getStoreName());
                    }
                })
        );
    }

    @FXML
    void backButtonAction(ActionEvent event) {
        accordian.setExpandedPane(storeTitledPane);
    }

    @FXML
    void nextFromChooseItemsButtonAction(ActionEvent event) {
        accordian.setExpandedPane(priceTitledPane);
    }

    @FXML
    void nextFromChooseStoresButtonAction(ActionEvent event) {
        accordian.setExpandedPane(itemsTitledPane);
    }


    public void bindToChildAnchorPane(AnchorPane childAnchorPane) {
        rootAnchorPane.prefWidthProperty().bind(childAnchorPane.widthProperty());
    }
}
