package components.Main;

import Logic.SDM.SDMFileVerifier;
import Logic.SDM.SDMManager;
import components.ViewInfo.ViewMainController;
import components.NewOrderMenu.NewOrderContainerController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;

public class MainAppController {

    @FXML
    private GridPane mainGridPane;

    @FXML
    private Button loadButton;

    @FXML
    private Button viewInfoButton;

    @FXML
    private Button newOrderButton;

    @FXML
    private Button updateButton;

    @FXML
    private Label fileLabel;
    @FXML
    private Label errorMessageLabel;


    @FXML
    private Label selectedFileName;

    @FXML
    private AnchorPane childAnchorPane;

    @FXML
    private GridPane orderMenuRef;

    @FXML
    private GridPane viewMenuRef;


    private SDMManager sdmManager;
    private Boolean hasLoadedSDMFile = false;
    private BooleanProperty isFileSelected;
    private BooleanProperty isNewOrderComplete;
    private SimpleStringProperty selectedFileProperty;
    private ViewMainController viewMainController;
    private NewOrderContainerController newOrderContainerController;
    private ChangeListener<Boolean> isNewOrderCompleteChangeListener;


    public MainAppController(){
        isFileSelected = new SimpleBooleanProperty(false);
        isNewOrderComplete = new SimpleBooleanProperty(false);
        selectedFileProperty = new SimpleStringProperty("");
        sdmManager = SDMManager.getInstance();

        //This tells MainContainer to replace the NewOrder FXML with the ViewInfo FXML when a New Order is complete
        isNewOrderComplete.addListener(((observable, oldValue, newValue) -> {
            if (newValue == true){
                loadNewPane(viewMenuRef);
                setIsNewOrderComplete(false);
            }
        }));
    }

    @FXML
    private void initialize(){
        System.out.println("Inside MainAppController initialize().");
        selectedFileName.textProperty().bind(selectedFileProperty);
        viewInfoButton.disableProperty().bind(isFileSelected.not());
        newOrderButton.disableProperty().bind(isFileSelected.not());
        updateButton.disableProperty().bind(isFileSelected.not());
    }

    public void setIsNewOrderComplete(boolean isNewOrderComplete) {
        this.isNewOrderComplete.set(isNewOrderComplete);
    }

    public boolean isIsNewOrderComplete() {
        return isNewOrderComplete.get();
    }

    public BooleanProperty isNewOrderCompleteProperty() {
        return isNewOrderComplete;
    }


    @FXML
    void loadButtonAction(ActionEvent event) {

        Window stage = mainGridPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose SDM file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));

        try {
            System.out.println("Inside try for loadButtonAction");
            File file = fileChooser.showOpenDialog(stage);
            fileChooser.setInitialDirectory(file.getParentFile());

            SDMFileVerifier sdmForChosenFile = new SDMFileVerifier(file);

            System.out.printf("Checking if %s file is valid...\n", file.getName());

            if (sdmForChosenFile.getIsValidFile()){
                System.out.println("Valid, yay!!!");
                //enableAllButtons();

                hasLoadedSDMFile = true;
                isFileSelected.set(true);
                selectedFileProperty.set(file.getAbsolutePath());
                sdmManager.loadNewSDMFile(sdmForChosenFile);

                errorMessageLabel.setText("");
                loadXMLForOtherButtons();
            } else{
                System.out.printf("File %s appears to be invalid (GASP!)\n", file.getName());
                errorMessageLabel.setText(sdmForChosenFile.getLoadingErrorMessage());
                errorMessageLabel.setTextFill(Color.RED);
                errorMessageLabel.setVisible(true);
            }

        } catch (Exception ex) {
            System.out.println("error");
        }
    }


    private void loadNewPane(GridPane paneToLoad) {
        childAnchorPane.getChildren().clear();
        childAnchorPane.getChildren().add(paneToLoad);
        AnchorPane.setBottomAnchor(paneToLoad, 0.0);
        AnchorPane.setLeftAnchor(paneToLoad, 0.0);
        AnchorPane.setRightAnchor(paneToLoad, 0.0);
        AnchorPane.setTopAnchor(paneToLoad, 0.0);
    }


    @FXML
    void newOrderAction(ActionEvent event) {
        //loadPage("/components/ordermenu/OrderMenu");
        System.out.println("Calling setCenterPane(orderMenuRef):");
        loadNewPane(orderMenuRef);
    }

    @FXML
    void updateButtonAction(ActionEvent event) {

    }

    @FXML
    void viewInfoButtonAction(ActionEvent event) {
        //loadPage("/components/viewMenu/ViewMenu");
        System.out.println("Calling setCenterPane(viewMenuRef):");
        loadNewPane(viewMenuRef);
    }

    private void loadXMLForOtherButtons() {
        System.out.println("Going to try and store ref to viewMenu.fxml");
        try {
            FXMLLoader viewLoader = new FXMLLoader();
            viewLoader.setLocation(getClass().getResource("/components/ViewInfo/ViewMainContainer.fxml"));
            viewMenuRef = viewLoader.load();
            viewMainController = viewLoader.getController();

            System.out.println("Going to try and store ref to orderMenu.fxml");

            FXMLLoader newOrderLoader = new FXMLLoader();
            newOrderLoader.setLocation(getClass().getResource("/components/NewOrderMenu/NewOrderContainer.fxml"));
            orderMenuRef = newOrderLoader.load();
            newOrderContainerController = newOrderLoader.getController();

            newOrderContainerController.bindIsCompleteOrder(isNewOrderComplete);
            isNewOrderComplete.bindBidirectional(newOrderContainerController.isOrderCompleteProperty());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
