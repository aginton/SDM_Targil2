package components.UpdateInventory;

import Logic.Inventory.Inventory;
import Logic.SDM.SDMManager;
import components.UpdateInventory.AddItemToStore.AddItemToStoreController;
import components.UpdateInventory.ChangeStoreItemPrice.ChangeStoreItemPriceController;
import components.UpdateInventory.RemoveItemFromStore.RemoveItemFromStoreController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class UpdateInventoryContainerController{
    @FXML
    private GridPane rootGridPane;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button removeButton;

    @FXML
    private AnchorPane childAnchorPane;

    @FXML
    void onAddButtonAction(ActionEvent event) {
        swapNode(addItemRef);
    }

    @FXML
    void onRemoveButtonAction(ActionEvent event) {
        swapNode(removeItemRef);
    }

    @FXML
    void onUpdateButtonAction(ActionEvent event) {
        swapNode(updatePriceRef);
    }

    public void swapNode(Node newCurrent){
        childAnchorPane.getChildren().clear();
        childAnchorPane.getChildren().add(newCurrent);
        AnchorPane.setBottomAnchor(newCurrent,0.0);
        AnchorPane.setLeftAnchor(newCurrent,0.0);
        AnchorPane.setRightAnchor(newCurrent,0.0);
        AnchorPane.setTopAnchor(newCurrent,0.0);
    }

    private Node addItemRef, removeItemRef, updatePriceRef;
    private AddItemToStoreController addItemToStoreController;
    private RemoveItemFromStoreController removeItemFromStoreController;
    private ChangeStoreItemPriceController changeStoreItemPriceController;


    private Inventory inventory;

    public UpdateInventoryContainerController(){
        inventory = SDMManager.getInstance().getInventory();
        try {
            FXMLLoader addItemLoader = new FXMLLoader();
            addItemLoader.setLocation(getClass().getResource("/components/UpdateInventory/AddItemToStore/AddItemToStore.fxml"));
            addItemRef = addItemLoader.load();
            addItemToStoreController = addItemLoader.getController();
//            addItemToStoreController.bindToChildAnchorPane(childAnchorPane);

            FXMLLoader removeItemLoader = new FXMLLoader();
            removeItemLoader.setLocation(getClass().getResource("/components/UpdateInventory/RemoveItemFromStore/RemoveItemFromStore.fxml"));
            removeItemRef = removeItemLoader.load();
            removeItemFromStoreController = removeItemLoader.getController();
            //removeItemFromStoreController.bindToChildAnchorPane(childAnchorPane);



            FXMLLoader changePriceLoader = new FXMLLoader();
            changePriceLoader.setLocation(getClass().getResource("/components/UpdateInventory/ChangeStoreItemPrice/ChangeStoreItemPrice.fxml"));
            updatePriceRef = changePriceLoader.load();
            changeStoreItemPriceController = changePriceLoader.getController();
            //changeStoreItemPriceController.bindToChildAnchorPane(childAnchorPane);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void refreshOthers() {

        addItemToStoreController.refresh();
        removeItemFromStoreController.refresh();
        changeStoreItemPriceController.refresh();
    }

    public void bindToMainChildAnchorPane(AnchorPane mainChildAnchorPane) {
        rootGridPane.prefWidthProperty().bind(mainChildAnchorPane.widthProperty());
        rootGridPane.prefHeightProperty().bind(mainChildAnchorPane.heightProperty());
    }
}
