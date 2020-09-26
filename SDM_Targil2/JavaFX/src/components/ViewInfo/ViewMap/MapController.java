package components.ViewInfo.ViewMap;

import Logic.Customers.Customer;
import Logic.Interfaces.hasLocationInterface;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import javafx.fxml.Initializable;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MapController implements Initializable {

    private static final String TAG = "MapController";
    @FXML
    private AnchorPane mapAnchorPane;
    @FXML
    private Label statusMsg;


    private List<Customer> customers;
    private List<Store> stores;
    private int maxXValue;
    private int maxYValue;

    private static final double ELEMENT_SIZE = 100;

    private int BOARDSIZE;
    private Cell[][] cell;
    private GridPane pane;
    private BorderPane borderPane;

    public MapController(){
        setUpBasicData();
    }

    private void setUpBasicData(){
        pane = new GridPane();
        maxXValue = 0;
        maxYValue = 0;
        customers = SDMManager.getInstance().getCustomers().getCustomers();
        stores = SDMManager.getInstance().getStores();
        updateMaxXandY(customers);
        updateMaxXandY(stores);
        this.BOARDSIZE = (maxXValue>maxYValue)? maxXValue:maxYValue;
        cell = new Cell[BOARDSIZE+1][BOARDSIZE+1];
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpFields();
    }

    private void setUpFields() {
        statusMsg.setText("Nothing to see...");

        for (int i=0; i<=BOARDSIZE; i++){
            for (int j=0; j<=BOARDSIZE;j++){
                cell[i][j] = new Cell(i,j);
                pane.add(cell[i][j],j,i);
            }
        }
        for (Store store: stores){
            int x = store.getX();
            int y = store.getY();
            cell[y][x].setType(1, store);
        }

        for (Customer customer: customers){
            int x = customer.getX();
            int y = customer.getY();
            cell[y][x].setType(2, customer);
        }

        mapAnchorPane.getChildren().add(pane);
        AnchorPane.setBottomAnchor(pane,0.0);
        AnchorPane.setLeftAnchor(pane,0.0);
        AnchorPane.setRightAnchor(pane,0.0);
        AnchorPane.setTopAnchor(pane,0.0);
    }

    //This method updates maxXValue and maxYValue if there are positions in input list that are larger than current max values
    public void updateMaxXandY(List<? extends hasLocationInterface> listToGoThrough){
        for (hasLocationInterface obj: listToGoThrough){
            if (obj.getX() > maxXValue){
                maxXValue = obj.getX();
                //System.out.println("maxXValue was changed to " + maxXValue + "!");
            }
            if (obj.getY() > maxYValue){
                maxYValue = obj.getY();
                //System.out.println("maxYValue was changed to " + maxYValue + "!");
            }

        }
    }

    public void refresh() {
        System.out.println(TAG + " - refresh()");
        mapAnchorPane.getChildren().clear();
        customers.clear();
        stores.clear();
        setUpBasicData();
        setUpFields();
    }

    public class Cell extends Pane{
        private int type;
        private String msg;
        private int x;
        private int y;


        public Cell(int i, int j){
            setStyle("-fx-border-color: black");
            //this.setPrefSize(200,200);
            this.setMinSize(50,50);
            this.setMaxSize(100,100);
            this.setOnMouseClicked(e->handleClick());
            type = 0;
            this.x = i;
            this.y = j;
            msg = "Nothing to show\nLocation: [" + i + ", " + j +"]";
        }

        public void setType(int t, Object arg){
            this.type = t;

            if (type == 1){
                Store store = (Store) arg;
                StringBuilder sb = new StringBuilder("Store")
                        .append("\nName: "+ store.getStoreName())
                        .append("\nLocation: "+ store.getLocation())
                        .append("\nNumber of orders: " + store.getStoreOrders().size());
                msg = sb.toString();

                Ellipse ellipse = new Ellipse(this.getWidth()/2, this.getHeight()/2, this.getWidth()/2-10, this.getHeight()/2 -10);
                ellipse.centerXProperty().bind(this.widthProperty().divide(2));
                ellipse.centerYProperty().bind(this.heightProperty().divide(2));
                ellipse.radiusXProperty().bind(this.widthProperty().divide(2).subtract(10));
                ellipse.radiusYProperty().bind(this.heightProperty().divide(2).subtract(10));
                ellipse.setStroke(Color.BLACK);
                ellipse.setFill(Color.RED);
                setStyle("-fx-background-color: hotpink");
                getChildren().add(ellipse);

                store.totalNumberOfOrdersProperty().addListener(((observable, oldValue, newValue) -> {
                    StringBuilder sb2 = new StringBuilder("Store")
                            .append("\nName: "+ store.getStoreName())
                            .append("\nLocation: "+ store.getLocation())
                            .append("\nNumber of orders: " + store.getStoreOrders().size());
                    msg = sb2.toString();
                }));
            }

            if (type == 2){
                Customer customer = (Customer) arg;
                StringBuilder sb = new StringBuilder("Customer")
                        .append("\nName: "+ customer.getCustomerName())
                        .append("\nLocation: "+ customer.getLocation())
                        .append("\nNumber of orders: " + customer.getOrders().size());
                msg = sb.toString();
                Ellipse ellipse = new Ellipse(this.getWidth()/2, this.getHeight()/2, this.getWidth()/2-10, this.getHeight()/2 -10);
                ellipse.centerXProperty().bind(this.widthProperty().divide(2));
                ellipse.centerYProperty().bind(this.heightProperty().divide(2));
                ellipse.radiusXProperty().bind(this.widthProperty().divide(2).subtract(10));
                ellipse.radiusYProperty().bind(this.heightProperty().divide(2).subtract(10));
                ellipse.setStroke(Color.BLACK);
                ellipse.setFill(Color.BLUE);
                getChildren().add(ellipse);
                setStyle("-fx-background-color: chocolate");
            }
        }

        private void handleClick() {
            statusMsg.setText(msg);
        }
    }
}
