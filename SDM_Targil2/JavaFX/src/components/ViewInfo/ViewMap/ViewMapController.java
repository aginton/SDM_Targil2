package components.ViewInfo.ViewMap;

import Logic.Customers.Customer;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import Logic.Interfaces.hasLocationInterface;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


//https://stackoverflow.com/questions/31095954/how-to-get-gridpane-row-and-column-ids-on-mouse-entered-in-each-cell-of-grid-in
//https://www.google.com/search?q=javafx+add+label+gridpane+x+y+coordinates&rlz=1C1EKKP_enUS760IL760&sxsrf=ALeKk03VOboLNwLYypm-xRPIlKQqjKveNA:1600323798318&source=lnms&tbm=isch&sa=X&ved=2ahUKEwiIsZjcxu_rAhVO_aQKHbtoBZYQ_AUoAXoECBoQAw&biw=1920&bih=1007#imgrc=_t2oQARJUl4BiM&imgdii=bIACUuTGBnmNbM


//READ
//https://stackoverflow.com/questions/25403267/how-to-maintain-gridpanes-fixed-size-after-adding-elemnts-dynamically

public class ViewMapController implements Initializable {

//    @FXML
//    private GridPane gridpane;

    @FXML
    private ScrollPane scrollp;


    private List<Customer> customers;
    private List<Store> stores;
    private int maxXValue;
    private int maxYValue;

    private static final double ELEMENT_SIZE = 100;
    private static final double GAP = ELEMENT_SIZE / 10;
    private ObjectProperty<Bounds> viewBounds;


    //        private Group display = new Group(tilePane);
    private int nRows;
    private int nCols;

    private GridPane grid;
    public static int TILE_SIZE = 100;
    public static int WIDTH = 8;
    public static int HEIGHT = 8;
    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    Color[] colors = {Color.GRAY, Color.BLUE, Color.GREEN, Color.RED};

    //https://stackoverflow.com/questions/55602778/how-to-add-objects-in-grid-from-bottom-up

    public ViewMapController(){
        maxXValue = 0;
        maxYValue = 0;
        customers = SDMManager.getInstance().getCustomers().getCustomers();
        stores = SDMManager.getInstance().getStores();
        updateMaxXandY(customers);
        updateMaxXandY(stores);
        WIDTH = maxYValue;
        HEIGHT = maxXValue;
        grid = new GridPane();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        grid.setPrefSize(WIDTH*TILE_SIZE, HEIGHT*TILE_SIZE);
        System.out.println("List of stores locations:");
        stores.forEach(i-> System.out.println(i.getLocation()));

        //prepareMap();
        createBoard();

        addStoresToGrid();
        addCustomersToGrid();
        scrollp.setContent(grid);
    }

    private void createBoard() {
        for (int row = 0; row < maxYValue; row++) {
            for (int col = 0; col < maxXValue; col++) {
                Tile tile = new Tile(col, row);
                Color color;
                if ((row + col) % 2 == 0) color = Color.WHITE;
                else color = Color.BLACK;
                tile.setFill(color);

                grid.add(tile, col, row);
                tile.getRectangle().widthProperty().bind(grid.widthProperty().divide(ELEMENT_SIZE));
                tile.getRectangle().heightProperty().bind(grid.heightProperty().divide(ELEMENT_SIZE));
            }
        }
    }


    private void addCustomersToGrid() {
        for (Customer customer: customers){
            int x = customer.getX();
            int y = customer.getY();

            Piece piece = new Piece(customer);
            Tile n = (Tile) getNodeByRowColumnIndex(y-1,x-1, grid);
            n.getChildren().add(piece);
        }
    }

    private void addStoresToGrid() {

        for (Store store: stores){
            int x = store.getX();
            int y = store.getY();
            Piece piece = new Piece(store);
            Tile n = (Tile) getNodeByRowColumnIndex(y-1,x-1, grid);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Piece.fxml"));
            n.getChildren().add(piece);

            //n.setFill(Color.RED);
        }
    }

    public Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();
        boolean isFound = false;

        for (Node node : childrens) {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                result = node;
                isFound = true;
                break;
            }
        }

        if (isFound)
            System.out.println("Found store at ("+(column+1) + ", " + (row+1) +")!");
        else
            System.out.println("didn't find it ):");

        return result;
    }




    //This method updates maxXValue and maxYValue if there are positions in input list that are larger than current max values
    public void updateMaxXandY(List<? extends hasLocationInterface> listToGoThrough){
        for (hasLocationInterface obj: listToGoThrough){
            if (obj.getX() > maxXValue){
                maxXValue = obj.getX();
                System.out.println("maxXValue was changed to " + maxXValue + "!");
            }
            if (obj.getY() > maxYValue){
                maxYValue = obj.getY();
                System.out.println("maxYValue was changed to " + maxYValue + "!");
            }

        }
    }

    public void prepareMap(){

        for (int y=0; y < maxYValue+1; y++){
            for (int x = 0; x < maxXValue+1; x++){
                Tile tile = new Tile(x,y);
                //board[x][y]=tile;

                GridPane.setRowIndex(tile, y);
                GridPane.setColumnIndex(tile, x);
                grid.getChildren().add(tile);
            }
        }
    }


}
