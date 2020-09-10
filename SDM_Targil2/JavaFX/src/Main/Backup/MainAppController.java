//package Main;
//
//import Logic.SDM.SDMFileVerifier;
//import Logic.SDM.SDMManager;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.paint.Color;
//import javafx.stage.FileChooser;
//import javafx.stage.Window;
//
//import java.io.File;
//import java.io.IOException;
//
//public class MainAppController {
//
//    @FXML
//    private HBox viewMenuRef, orderMenuRef;
//
//    @FXML
//    private Label testLabel;
//
//    @FXML
//    private Label errorMessageLabel;
//
//    @FXML
//    private BorderPane mainBorderPane;
//
//    @FXML
//    private Button loadButton;
//
//    @FXML
//    private Button viewButton;
//
//    @FXML
//    private Button newOrderButton;
//
//    @FXML
//    private Button updateButton;
//
//    private SDMManager sdmManager;
//    private Boolean hasLoadedSDMFile = false;
//
//
//    @FXML
//    void loadButtonAction(ActionEvent event) {
//
//        Window stage = mainBorderPane.getScene().getWindow();
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Choose SDM file");
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
//
//        try {
//            System.out.println("Inside try for loadButtonAction");
//            File file = fileChooser.showOpenDialog(stage);
//            fileChooser.setInitialDirectory(file.getParentFile());
//
//            SDMFileVerifier sdmForChosenFile = new SDMFileVerifier(file);
//
//            System.out.printf("Checking if %s file is valid...\n", file.getName());
//
//            if (sdmForChosenFile.getIsValidFile()){
//                System.out.println("Valid, yay!!!");
//                hasLoadedSDMFile = true;
//                //file loaded successfully
//                testLabel.setVisible(true);
//                testLabel.setText(file.getName());
//                testLabel.setTextFill(Color.GREEN);
//                errorMessageLabel.setText("");
//                sdmManager.loadNewSDMFile(sdmForChosenFile);
//                loadXMLForOtherButtons();
//            } else{
//                System.out.printf("File %s appears to be invalid (GASP!)\n", file.getName());
//                errorMessageLabel.setText(sdmForChosenFile.getLoadingErrorMessage());
//                errorMessageLabel.setTextFill(Color.RED);
//                errorMessageLabel.setVisible(true);
//            }
//
//        } catch (Exception ex) {
//            System.out.println("error");
//        }
//    }
//
//    private void loadXMLForOtherButtons() {
//        if (hasLoadedSDMFile){
//            System.out.println("Going to try and store ref to viewMenu.fxml");
//            try {
//                viewMenuRef = FXMLLoader.load(getClass().getResource("/components/viewMenu/ViewMenu.fxml"));
//                System.out.println("Going to try and store ref to orderMenu.fxml");
//                orderMenuRef = FXMLLoader.load(getClass().getResource("/components/ordermenu/OrderMenu.fxml"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @FXML
//    void newOrderAction(ActionEvent event) {
//
//        //loadPage("/components/ordermenu/OrderMenu");
//        System.out.println("Calling setCenterPane(orderMenuRef):");
//        setCenterPane(orderMenuRef);
//    }
//
//    private void setCenterPane(Parent newCenter) {
//        mainBorderPane.setCenter(newCenter);
//    }
//
//    @FXML
//    void updateButtonAction(ActionEvent event) {
//
//    }
//
//    @FXML
//    void viewButtonAction(ActionEvent event) {
//
//        //loadPage("/components/viewMenu/ViewMenu");
//        System.out.println("Calling setCenterPane(viewMenuRef):");
//        setCenterPane(viewMenuRef);
//
//    }
//
//    private void loadPage(String pageName) {
//
//        Parent root  = null;
//
//        try {
//            root = FXMLLoader.load(getClass().getResource(pageName + ".fxml"));
//        } catch (IOException e) {
//            System.out.println("ERROR!!!! Exception thrown in function loadPage");
//        }
//
//        mainBorderPane.setCenter(root);
//    }
//
//
//    @FXML
//    private void initialize(){
//        System.out.println("Inside MainAppController initialize().");
//        sdmManager = SDMManager.getInstance();
//    }
//
//}
