package components.ViewInfo.ViewOrders.SingleOrder;

import Logic.Order.CartItem;
import Logic.Order.Order;
import Logic.Store.Store;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SingleOrderViewController  {
    @FXML
    private Label orderIdLabel;

    @FXML
    private Label orderDateLabel;

    @FXML
    private Label storeIdLabel;

    @FXML
    private Label storeNameLabel;

    @FXML
    private TableView<CartItem> itemTableView;

    @FXML
    private TableColumn<CartItem, Integer> itemIdColumn;

    @FXML
    private TableColumn<CartItem, String> itemNameColumn;

    @FXML
    private TableColumn<CartItem, Integer> unitPriceColumn;

    @FXML
    private TableColumn<CartItem, Float> amountColumn;

    @FXML
    private TableColumn<CartItem, String> costColumn;

    @FXML
    private Label totalNumItemsLabel;

    @FXML
    private Label totalTypeItemsLabel;

    @FXML
    private Label totalNumberStoresLabel;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label deliveryFeeLabel;

    @FXML
    private Label totalLabel;

    private Order order;
    private ObservableList<CartItem> items = FXCollections.observableArrayList();


    public void setOrder(Order order){
        this.order = order;
        //System.out.println("Do something with this order");
        setLabels(order);
        setOrderTable(order);
    }

    private void setOrderTable(Order order) {
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Integer>("itemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<CartItem,String>("itemName"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Float>("itemAmount"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Integer>("price"));
        costColumn.setCellValueFactory(cellData ->{
            CartItem item = cellData.getValue();
            String res = String.format("%.2f",item.getItemAmount()* item.getPrice());
            return new ReadOnlyStringWrapper(res);
        });

        order.getCartForThisOrder().getCart().values().forEach(item->items.add(item));
        order.getCartForThisOrder().getDiscountCart().values().forEach(item->items.add(item));
        itemTableView.setItems(items);
    }

    private void setLabels(Order order) {
        orderIdLabel.setText(order.getOrderId().toString());
        orderDateLabel.setText(order.getOrderDate().toString());

        StringBuilder storeNames = new StringBuilder("");
        StringBuilder storeIds = new StringBuilder("");
        for (Store store: order.getStoresBoughtFrom()){
            storeIds.append(store.getStoreId() +", ");
            storeNames.append(store.getStoreName() + ", ");
        }
        storeIdLabel.setText(storeIds.toString());
        storeNameLabel.setText(storeNames.toString());
        totalNumItemsLabel.setText(String.valueOf(order.getNumberOfItemsInOrder()));
        totalTypeItemsLabel.setText(String.valueOf(order.getNumberItemsByType()));
        totalNumberStoresLabel.setText(String.valueOf(order.getStoresBoughtFrom().size()));
        subtotalLabel.setText(String.valueOf(order.getCartTotal()));
        deliveryFeeLabel.setText(String.valueOf(order.getTotalDeliveryCost()));
        totalLabel.setText(String.valueOf(order.getTotalOrderCost()));
    }


}
