package components.UpdateInventory;

import Logic.Inventory.Inventory;
import Logic.SDM.SDMManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class UpdateInventoryContainerController{

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

    private Inventory inventory;

    public UpdateInventoryContainerController(){
        inventory = SDMManager.getInstance().getInventory();
        try {
            addItemRef = FXMLLoader.load(getClass().getResource("/components/UpdateInventory/AddItemToStore/AddItemToStore.fxml"));
            removeItemRef = FXMLLoader.load(getClass().getResource("/components/UpdateInventory/RemoveItemFromStore/RemoveItemFromStore.fxml"));
            updatePriceRef = FXMLLoader.load(getClass().getResource("/components/UpdateInventory/ChangeStoreItemPrice/ChangeStoreItemPrice.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
