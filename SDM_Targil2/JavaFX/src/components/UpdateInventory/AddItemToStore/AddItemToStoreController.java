package components.UpdateInventory.AddItemToStore;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class AddItemToStoreController {

    @FXML
    private TitledPane chooseStoreTitledPane;

    @FXML
    private ComboBox<?> chooseCustomerCB;

    @FXML
    private TitledPane chooseItemTitledPane;

    @FXML
    private TableView<?> itemTableView;

    @FXML
    private TableColumn<?, ?> radioButtonColumn;

    @FXML
    private TableColumn<?, ?> idColumn;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private TableColumn<?, ?> categoryColumn;

    @FXML
    private TitledPane choosePriceTitledPane;

    @FXML
    private TextField priceTextField;

}
