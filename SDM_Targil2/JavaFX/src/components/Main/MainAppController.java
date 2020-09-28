package components.Main;

import Logic.SDM.SDMManager;
import Logic.SDM.SDMVerifierService;
import components.PlaceAnOrder.PlaceAnOrderMain.NewOrderContainerController;
import components.UpdateInventory.UpdateInventoryContainerController;
import components.ViewInfo.ViewMainController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;

public class MainAppController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label filePath;

    @FXML
    private Button viewButton;

    @FXML
    private Button placeAnOrderButton;

    @FXML
    private Button updateButton;

    @FXML
    private AnchorPane mainChildAnchorPane;


    private Node orderMenuRef, viewMenuRef, updateRef, homePageRef;
    private ViewMainController viewMainController;
    private NewOrderContainerController newOrderContainerController;
    private UpdateInventoryContainerController updateController;
    private HomePageController homePageController;

    private SDMManager sdmManager;
    //private Boolean hasLoadedSDMFile = false;
    private BooleanProperty isFileLoaded;
    private SimpleStringProperty selectedFileProperty;



    public MainAppController(){
        isFileLoaded = new SimpleBooleanProperty(false);
        selectedFileProperty = new SimpleStringProperty("");
        sdmManager = SDMManager.getInstance();


    }

    @FXML
    private void initialize(){
        System.out.println("Inside MainAppController initialize().");
        filePath.textProperty().bind(selectedFileProperty);
        viewButton.disableProperty().bind(isFileLoaded.not());
        placeAnOrderButton.disableProperty().bind(isFileLoaded.not());
        updateButton.disableProperty().bind(isFileLoaded.not());


        try {
            FXMLLoader homePageLoader = new FXMLLoader();
            homePageLoader.setLocation(getClass().getResource("/components/Main/HomePage.fxml"));
            homePageRef = homePageLoader.load();
            homePageController = homePageLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        filePath.textProperty().bind(homePageController.selectedFilePropertyProperty());
//        viewButton.disableProperty().bind(homePageController.isFileSelectedProperty().not());
//        placeAnOrderButton.disableProperty().bind(homePageController.isFileSelectedProperty().not());
//        updateButton.disableProperty().bind(homePageController.isFileSelectedProperty().not());

    }


    @FXML
    void loadButtonAction(ActionEvent event) {

        Window stage = mainBorderPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose SDM file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));

        swapPanes(homePageRef);
        File file = fileChooser.showOpenDialog(stage);

        if (file == null)
            return;

        if (file.getAbsolutePath().equals(selectedFileProperty.get())){
            System.out.println("This file is already loaded!");
            return;
        }

        SDMVerifierService service = new SDMVerifierService(file);
        homePageController.getStatusLabel().textProperty().bind(service.messageProperty());
        homePageController.getProgressBar().progressProperty().bind(service.progressProperty());
        service.setOnSucceeded(e->{
            Boolean ans = service.getValue();
            System.out.println("service.getValue() returned..." + ans);
            if (ans == true){
                selectedFileProperty.set(file.getAbsolutePath());
                if (!isFileLoaded()){
                    System.out.println("Loading others for first time");
                    loadXMLForOtherButtons();
                }
                if (isFileLoaded()){
                    //mainChildAnchorPane.getChildren().clear();

                    System.out.println("Resetting others");
                    refreshOthers();

                }
                //hasLoadedSDMFile = true;
                setIsFileLoaded(true);
            }
        });
        service.start();
    }

    private void refreshOthers() {
        newOrderContainerController.refreshOthers();
        viewMainController.refreshOthers();
        updateController.refreshOthers();
    }


    private void swapPanes(Node paneToLoad) {
        mainChildAnchorPane.getChildren().clear();
        mainChildAnchorPane.getChildren().add(paneToLoad);
        AnchorPane.setBottomAnchor(paneToLoad, 0.0);
        AnchorPane.setLeftAnchor(paneToLoad, 0.0);
        AnchorPane.setRightAnchor(paneToLoad, 0.0);
        AnchorPane.setTopAnchor(paneToLoad, 0.0);
    }

    private void loadXMLForOtherButtons() {
        //System.out.println("Going to try and store ref to viewMenu.fxml");
        try {
            FXMLLoader viewLoader = new FXMLLoader();
            viewLoader.setLocation(getClass().getResource("/components/ViewInfo/ViewMainContainer.fxml"));
            viewMenuRef = viewLoader.load();
            viewMainController = viewLoader.getController();
            viewMainController.bindToMainChildAnchorPane(mainChildAnchorPane);

            //  System.out.println("Going to try and store ref to orderMenu.fxml");

            FXMLLoader newOrderLoader = new FXMLLoader();
            newOrderLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/PlaceAnOrderMain/NewOrderContainer.fxml"));
            orderMenuRef = newOrderLoader.load();
            newOrderContainerController = newOrderLoader.getController();
            newOrderContainerController.bindToMainChildAnchorPane(mainChildAnchorPane);

            FXMLLoader updateLoader = new FXMLLoader();
            updateLoader.setLocation(getClass().getResource("/components/UpdateInventory/UpdateInventoryContainer.fxml"));
            updateRef = updateLoader.load();
            updateController = updateLoader.getController();
            updateController.bindToMainChildAnchorPane(mainChildAnchorPane);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void HomeButtonAction(ActionEvent event) {
        swapPanes(homePageRef);
    }

    @FXML
    void PlaceAnOrderAction(ActionEvent event) {
        //System.out.println("Calling setCenterPane(orderMenuRef):");
        swapPanes(orderMenuRef);
    }

    @FXML
    void UpdateAction(ActionEvent event) {
        swapPanes(updateRef);

    }

    @FXML
    void ViewButtonAction(ActionEvent event) {
        //loadPage("/components/viewMenu/ViewMenu");
        //System.out.println("Calling setCenterPane(viewMenuRef):");
        swapPanes(viewMenuRef);
    }

    public boolean isFileLoaded() {
        return isFileLoaded.get();
    }

    public BooleanProperty isFileLoadedProperty() {
        return isFileLoaded;
    }

    public void setIsFileLoaded(boolean isFileLoaded) {
        this.isFileLoaded.set(isFileLoaded);
    }
}





