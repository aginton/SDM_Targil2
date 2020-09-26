package components.Main;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.File;

public class LoadPaneController {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    private Boolean hasLoadedSDMFile = false;
    private BooleanProperty isFileSelected;
    private SimpleStringProperty selectedFileProperty;

    public LoadPaneController(){
        isFileSelected = new SimpleBooleanProperty(false);
        selectedFileProperty = new SimpleStringProperty("");
    }

    public void tryLoadingFile(File file){

    }

}
