package components.ViewInfo.ViewMap;

import Logic.Customers.Customer;
import Logic.Interfaces.hasLocationInterface;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TilePaneMapController implements Initializable{

    @FXML
    private GridPane gridPane;

    @FXML
    private TilePane tilePane;

    SDMManager sdm = SDMManager.getInstance();
    List<Store> stores;
    List<Customer> customers;

    List<hasLocationInterface> locationables;
    List<Integer> mapMaxCoordinates;
    hasLocationInterface[][] locationableMatrix;
    private int MAPWIDTH;

    public TilePaneMapController () {

        stores = sdm.getStores();
        customers = sdm.getCustomers().getCustomers();
        locationables = createLocationableList();
        mapMaxCoordinates = getLargestXYCoordinatesForMap(locationables);
        locationableMatrix = createLocationableMatrix(locationables,mapMaxCoordinates);
    }

    private hasLocationInterface[][] createLocationableMatrix(List<hasLocationInterface> locationables, List<Integer> mapMaxCoordinates) {

        int rows = mapMaxCoordinates.get(1);
        int cols = mapMaxCoordinates.get(0);
        this.MAPWIDTH = (rows>cols)? rows: cols;
        hasLocationInterface[][] matrix = new hasLocationInterface[MAPWIDTH+1][MAPWIDTH+1];

        //initialize matrix with null
//        for(int i = 0; i < rows; i++)
//            for(int j = 0; j < cols; j++)
//                matrix[i][j] = null;

        locationables.forEach((hasLoc) -> {
            int col = hasLoc.getX();
            int row = hasLoc.getY();
            matrix[row][col] = hasLoc;
        });


        return matrix;
    }

    private List<Integer> getLargestXYCoordinatesForMap(List<hasLocationInterface> locationables) {

        List<Integer> mapMaxCoordinates = new ArrayList<>();
        int largestX = 0, largestY = 0;

        for (hasLocationInterface locationable: locationables) {
            if (locationable.getX() > largestX) {
                largestX = locationable.getX();
            }

            if (locationable.getY() > largestY) {
                largestY = locationable.getY();
            }
        }

        mapMaxCoordinates.add(largestX);
        mapMaxCoordinates.add(largestY);

        return mapMaxCoordinates;
    }

    private List<hasLocationInterface> createLocationableList() {

        //adding all stores and customers to this list
        List<hasLocationInterface> locationableList = new ArrayList<>();

        for (Store store: stores){
            locationableList.add(store);
        }

        for (Customer customer: customers) {
            locationableList.add(customer);
        }

        return locationableList;

    }

    private StackPane buildCell(hasLocationInterface locationable){

        StackPane pane = new StackPane();
        pane.setPrefSize(25, 25);

        if (locationable instanceof Store) {
            pane.setStyle("-fx-background-color: #a83c32");

        }
        else if (locationable instanceof Customer) {
            pane.setStyle("-fx-background-color: #32a851");
        }
        else {
            pane.setStyle("-fx-background-color: #000000");
        }

        pane.getChildren().add(new Rectangle(5,5));
        // add labels to stackpane
        //Label stitchLabel = new Label("M");

        //create rectangle to color stackpane
        //Rectangle rect = new Rectangle (5, 5); //set rectangle to same size as stackpane

        //pane.getChildren().add(rect);

        return pane;
    }

    protected void createTiles(hasLocationInterface[][] locationableMatrix, int MAPWIDTH){

        tilePane.setPrefColumns(MAPWIDTH*5); //set prefcolumns to the array width

        //add cells to tilepane
        for (int i=0; i<MAPWIDTH; i++){
            for (int j=0; j<MAPWIDTH; j++){
                StackPane cell = buildCell(locationableMatrix[i][j]);
                tilePane.getChildren().add(cell);
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        createTiles(locationableMatrix, MAPWIDTH);

    }
}
