package components.ViewInfo.ViewMap;

import Logic.Customers.Customer;
import Logic.Interfaces.hasLocationInterface;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MapController implements Initializable {

    private static final String TAG = "MapController";
    @FXML
    private AnchorPane mapAnchorPane;

    private List<Customer> customers;
    private List<Store> stores;
    private int maxXValue;
    private int maxYValue;

    private static final double ELEMENT_SIZE = 100;

    //private int XBoardSize = 0;
    //private int YBoardSize = 0;
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
        customers = SDMManager.getInstance().getCustomers().getListOfCustomers();
        stores = SDMManager.getInstance().getStores();
        updateMaxXandY(customers);
        updateMaxXandY(stores);
       // this.BOARDSIZE = (maxXValue>maxYValue)? maxXValue:maxYValue;
        cell = new Cell[maxXValue+1][maxYValue+1];
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpFields();
    }

    private void setUpFields() {
        //statusMsg.setText("Nothing to see...");

        for (int i = maxXValue; i >= 0; i--){
            for (int j = 0; j <= maxYValue; j++){
                cell[i][j] = new Cell(i,j);
                pane.add(cell[i][j],j,maxXValue-i);
            }
        }
        for (Store store: stores){
            int x = store.getX();
            int y = store.getY();
            cell[x][y].setType(eMapElementType.STORE, store, cell[x][y]);
        }

        for (Customer customer: customers){
            int x = customer.getX();
            int y = customer.getY();
            cell[x][y].setType(eMapElementType.CUSTOMER, customer, cell[x][y]);
        }

        mapAnchorPane.getChildren().add(pane);
//        pane.maxHeightProperty().bind(mapAnchorPane.heightProperty());
//        pane.minHeightProperty().bind(mapAnchorPane.heightProperty());
//        pane.maxWidthProperty().bind(mapAnchorPane.widthProperty());
//        pane.minWidthProperty().bind(mapAnchorPane.widthProperty());
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
        private eMapElementType elementType;
        private String msg;
        private int x;
        private int y;


        public Cell(int i, int j) {
            setStyle("-fx-border-color: #828181");
            //this.setPrefSize(200,200);

//            this.maxHeightProperty().bind(mapAnchorPane.heightProperty().divide(10));
//            this.minHeightProperty().bind(mapAnchorPane.heightProperty().divide(10));
//            this.maxWidthProperty().bind(mapAnchorPane.widthProperty().divide(10));
//            this.minWidthProperty().bind(mapAnchorPane.widthProperty().divide(10));
            this.setMinSize(40,40);
            this.setMaxSize(40,40);

            //this.setOnMouseClicked(e->handleClick());
            elementType = eMapElementType.EMPTY;
            this.x = i;
            this.y = j;
            msg = "Nothing to show\nLocation: [" + i + ", " + j +"]";

            //tooltip unique key
            StringBuilder cellCoord = new StringBuilder();
            cellCoord.append("(").append(i).append(",").append(j).append(")");

            //assign tooltip(i,j) to this cell
            Tooltip tooltip = new Tooltip(msg);
            this.getProperties().put(cellCoord.toString(), tooltip);
            Tooltip.install(this,tooltip);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setType(eMapElementType elementType, Object arg, Cell cell){
            this.elementType = elementType;
            Tooltip tooltip;
            ImageView storeIcon;

            if (elementType == eMapElementType.STORE){
                Store store = (Store) arg;
                StringBuilder sb = new StringBuilder("Store")
                        .append("\nName: "+ store.getStoreName())
                        .append("\nLocation: "+ store.getLocation())
                        .append("\nNumber of orders: " + store.getStoreOrders().size());
                msg = sb.toString();
//                Ellipse ellipse = new Ellipse(this.getWidth()/2, this.getHeight()/2, this.getWidth()/2-10, this.getHeight()/2 -10);
//                ellipse.centerXProperty().bind(this.widthProperty().divide(2));
//                ellipse.centerYProperty().bind(this.heightProperty().divide(2));
//                ellipse.radiusXProperty().bind(this.widthProperty().divide(2).subtract(10));
//                ellipse.radiusYProperty().bind(this.heightProperty().divide(2).subtract(10));
//                ellipse.setStroke(Color.BLACK);
//                ellipse.setFill(Color.RED);

                storeIcon = new ImageView("/resources/map_icons/store.png");
                storeIcon.fitWidthProperty().bind(cell.widthProperty());
                storeIcon.fitHeightProperty().bind(cell.heightProperty());

                //setStyle("-fx-background-color: hotpink");
                getChildren().add(storeIcon);

                store.totalNumberOfOrdersProperty().addListener(((observable, oldValue, newValue) -> {
                    StringBuilder sb2 = new StringBuilder("Store")
                            .append("\nID: " + store.getStoreId())
                            .append("\nName: "+ store.getStoreName())
                            .append("\nLocation: "+ store.getLocation())
                            .append("\nNumber of orders: " + store.getStoreOrders().size());
                    msg = sb2.toString();
                }));
            }

            if (elementType == eMapElementType.CUSTOMER){
                Customer customer = (Customer) arg;
                StringBuilder sb = new StringBuilder("Customer")
                        .append("\nID: " + customer.getCustomerId())
                        .append("\nName: "+ customer.getCustomerName())
                        .append("\nLocation: "+ customer.getLocation())
                        .append("\nNumber of orders: " + customer.getOrders().size());
                msg = sb.toString();
//                Ellipse ellipse = new Ellipse(this.getWidth()/2, this.getHeight()/2, this.getWidth()/2-10, this.getHeight()/2 -10);
//                ellipse.centerXProperty().bind(this.widthProperty().divide(2));
//                ellipse.centerYProperty().bind(this.heightProperty().divide(2));
//                ellipse.radiusXProperty().bind(this.widthProperty().divide(2).subtract(10));
//                ellipse.radiusYProperty().bind(this.heightProperty().divide(2).subtract(10));
//                ellipse.setStroke(Color.BLACK);
//                ellipse.setFill(Color.BLUE);

                storeIcon = new ImageView("/resources/map_icons/customer.png");
                storeIcon.fitWidthProperty().bind(cell.widthProperty());
                storeIcon.fitHeightProperty().bind(cell.heightProperty());
                getChildren().add(storeIcon);
                //setStyle("-fx-background-color: chocolate");
            }

            StringBuilder cellCoord = new StringBuilder();
            cellCoord.append("(").append(cell.getX()).append(",").append(cell.getY()).append(")");

            //update tooltip text
            tooltip = (Tooltip) cell.getProperties().get(cellCoord.toString());
            tooltip.setText(msg);
        }

//        private void handleClick() {
//            statusMsg.setText(msg);
//        }
    }
}
