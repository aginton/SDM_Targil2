package components.viewInfoMenu.OrderView.SingleOrder;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class SingleOrderViewController {
    @FXML
    private Label orderIdLabel;

    @FXML
    private Label orderDateLabel;

    @FXML
    private Label storeIdLabel;

    @FXML
    private Label storeNameLabel;

    @FXML
    private TableView<?> itemTableView;

    @FXML
    private TableColumn<?, ?> itemIdColumn;

    @FXML
    private TableColumn<?, ?> itemNameColumn;

    @FXML
    private TableColumn<?, ?> unitPriceColumn;

    @FXML
    private TableColumn<?, ?> amountColumn;

    @FXML
    private TableColumn<?, ?> costColumn;

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

}
