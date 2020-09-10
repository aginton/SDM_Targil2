package components.newOrderMenu;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

public class OrderMenuController {

    @FXML
    private Label chooseCustomerLabel;

    @FXML
    private Label chooseDateLabel;

    @FXML
    private Label chooseOrderTypeLabel;

    @FXML
    private Label storeLabel;

    @FXML
    private Label addItemsLabel;

    @FXML
    private Label chooseSalesLabel;

    @FXML
    private Label confirmLabel;

    @FXML
    private Button backBtn;

    @FXML
    private Button nextBtn;

    @FXML
    private Button confirmBtn;

    private List<Label> labels;
    private IntegerProperty activeLabelID = new SimpleIntegerProperty(0);

    //https://stackoverflow.com/questions/51392203/what-does-initialize-mean-in-javafx
    @FXML
    private void initialize(){
        System.out.println("Inside OrderMenuController initialize() method!");

        labels = new ArrayList<>();
        labels.add(chooseCustomerLabel);
        labels.add(chooseDateLabel);
        labels.add(chooseOrderTypeLabel);
        labels.add(storeLabel);
        labels.add(addItemsLabel);
        labels.add(chooseSalesLabel);
        labels.add(confirmLabel);

        nextBtn.disableProperty().bind(activeLabelID.isEqualTo(labels.size()-1));
        backBtn.disableProperty().bind(activeLabelID.isEqualTo(0));
        confirmBtn.disableProperty().bind(activeLabelID.isNotEqualTo(labels.size()-1));

        activeLabelID.addListener(((observable, oldValue, newValue) -> {
            System.out.println("activeLabelID changed: oldValue = " + oldValue + ", newValue = " + newValue);
        }));

        labels.get(activeLabelID.get()).getStyleClass().add("selected");
    }

    @FXML
    void nextBtnAction(ActionEvent event) {
        labels.get(activeLabelID.get()).getStyleClass().remove("selected");
        activeLabelID.set(activeLabelID.getValue()+1);
        labels.get(activeLabelID.get()).getStyleClass().add("selected");
    }

    @FXML
    void backBtnAction(ActionEvent event) {
        labels.get(activeLabelID.get()).getStyleClass().remove("selected");
        activeLabelID.set(activeLabelID.getValue()-1);
        labels.get(activeLabelID.get()).getStyleClass().add("selected");
    }


}
