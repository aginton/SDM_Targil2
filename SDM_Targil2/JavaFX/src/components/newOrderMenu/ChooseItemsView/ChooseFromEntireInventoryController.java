package components.newOrderMenu.ChooseItemsView;

import Logic.Inventory.InventoryItem;
import Logic.Order.CartItem;
import Logic.Order.StoreItem;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.FloatStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class ChooseFromEntireInventoryController implements Initializable {
    @FXML
    private TableView<CartItem> inventoryTableView;

    @FXML
    private TableColumn<CartItem, Integer> itemIdColumn;

    @FXML
    private TableColumn<CartItem, String> itemNameColumn;

    @FXML
    private TableColumn<CartItem, Float> itemAmountColumn;

    @FXML
    private TableColumn<CartItem, Void> addColumn;

    @FXML
    private TableColumn<CartItem, CartItem> removeColumn;


    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private ChangeListener<CartItem> cartItemChangeListener;
    private CartItem selectedCartItem;

    public ChooseFromEntireInventoryController(){
        for (InventoryItem item: SDMManager.getInstance().getInventory().getListInventoryItems()){
            cartItems.add(new CartItem(item,0,0,null));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inventoryTableView.setItems(cartItems);

        itemIdColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Integer>("inventoryItemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<CartItem,String>("itemName"));
        itemAmountColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Float>("itemAmount"));

        addButtonToTable();
        addRemoveButtonToTable();
        inventoryTableView.setEditable(true);
        itemAmountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));


//        inventoryTableView.selectionModelProperty().addListener(((observable, oldValue, newValue) -> {
//            System.out.println("Listener was called!");
//            int index = inventoryTableView.getSelectionModel().getSelectedIndex();
//            selectedCartItem = inventoryTableView.getItems().get(index);
//            if (selectedCartItem != null){
//                System.out.println("Something changed for " + selectedCartItem);
//            }
//        }));

        inventoryTableView.getSelectionModel().selectedItemProperty().addListener(

                cartItemChangeListener = (((observable, oldValue, newValue) -> {
                    selectedCartItem = newValue;

                    if (newValue != null){
                        System.out.println("Cart item changed to " + selectedCartItem);
                    }
                }))
        );

    }

    //This method lets user double click on amount and change value
    public void changeAmountCellEvent(TableColumn.CellEditEvent edittedCell){
        //Idea: When double click on cell of item, you want to "Select" the object, update it, and save it
        CartItem selectedItem = inventoryTableView.getSelectionModel().getSelectedItem();
        selectedItem.setItemAmount((Float) edittedCell.getNewValue());
    }

    private void addRemoveButtonToTable() {
//        TableColumn<CartItem, CartItem> removeCol = new TableColumn("Remove");
        removeColumn.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        removeColumn.setCellFactory(param -> new TableCell<CartItem,CartItem>(){
            private final Button removeButton = new Button("remove");

            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null){
                    setGraphic(null);
                    return;
                }
                setGraphic(removeButton);
                removeButton.setOnAction(
                        event-> {
                            item.decreaseAmount(1);
                        }
                );
            }
        });


//        inventoryTableView.getColumns().add(removeColumn);

    }

    private void addButtonToTable() {
//        TableColumn<CartItem, Void> colBtn = new TableColumn("Button Column");

        Callback<TableColumn<CartItem, Void>, TableCell<CartItem, Void>> cellFactory = new Callback<TableColumn<CartItem, Void>, TableCell<CartItem, Void>>() {
            @Override
            public TableCell<CartItem, Void> call(final TableColumn<CartItem, Void> param) {
                final TableCell<CartItem, Void> cell = new TableCell<CartItem, Void>() {

                    private final Button btn = new Button("Add");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            CartItem selectedItem = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData old amount: " + selectedItem.getItemAmount());
                            selectedItem.increaseAmount(1);
                            System.out.println("selectedData new amount: " + selectedItem.getItemAmount());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        addColumn.setCellFactory(cellFactory);

//        inventoryTableView.getColumns().add(colBtn);

    }
}
