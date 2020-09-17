package components.ViewInfo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ViewOptionsController {

    @FXML
    private Button itemsButton;

    @FXML
    private Button storesButton;

    @FXML
    private Button ordersHistoryButton;

    @FXML
    private Pane mainPane;

    @FXML
    void ItemsAction(ActionEvent event) {
        viewItems();

    }

    @FXML
    void OrdersHistoryAction(ActionEvent event) {
    }

    @FXML
    void StoresAction(ActionEvent event) {
        viewStores();

    }

    private void viewItems() {

        mainPane.getChildren().clear();

        try {
            Node node = FXMLLoader.load(getClass().getResource("/components/ViewItems/ViewItems.fxml"));
            mainPane.getChildren().add(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void viewStores() {

        mainPane.getChildren().clear();

        Node [] nodes = new  Node[5];

        for(int i = 0; i<5; i++)
        {
            try {
                    nodes[i] = FXMLLoader.load(getClass().getResource("/components/ViewStores/StoreElement.fxml"));
                mainPane.getChildren().add(nodes[i]);

            } catch (IOException ex) {
                System.out.println("ERROR in viewStores method");
            }

        }

    }

    @FXML
    private void initialize(){
        System.out.println("Inside ViewOptions initialize().");
    }

}
