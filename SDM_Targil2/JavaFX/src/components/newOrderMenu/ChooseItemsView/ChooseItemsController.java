package components.newOrderMenu.ChooseItemsView;

//https://stackoverflow.com/questions/30889732/javafx-tableview-change-row-color-based-on-column-value


import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ChooseItemsController {
    @FXML
    private TableView<?> itemsTableView;

    @FXML
    private TableColumn<?, ?> itemIdColumn;

    @FXML
    private TableColumn<?, ?> itemNameColumn;

    @FXML
    private TableColumn<?, ?> categoryColumn;

    @FXML
    private TableColumn<?, ?> priceColumn;

    @FXML
    private TableColumn<?, ?> amountColumn;

    @FXML
    private TableColumn<?, ?> addButtonColumn;

    @FXML
    private TableColumn<?, ?> removeButtonColumn;
}
