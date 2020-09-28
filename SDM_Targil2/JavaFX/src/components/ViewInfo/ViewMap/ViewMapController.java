package components.ViewInfo.ViewMap;

import Logic.Customers.Customer;
import Logic.Customers.Customers;
import Logic.Interfaces.customersChangeListener;
import Logic.Interfaces.hasLocationInterface;
import Logic.Order.Order;
import Logic.Order.StoreItem;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import Logic.Store.StoreChangeListener;
import javafx.fxml.Initializable;
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

public class ViewMapController implements Initializable, customersChangeListener, StoreChangeListener {

    private static final String TAG = "MapController";
    @FXML
    private AnchorPane mapAnchorPane;

    private SDMManager sdmManager;
    private Customers allCustomers;
    private List<Customer> customers;
    private List<Store> stores;
    private int maxXValue;
    private int maxYValue;

    //private int XBoardSize = 0;
    //private int YBoardSize = 0;
    private Cell[][] cell;
    private GridPane pane;

    public ViewMapController(){

        sdmManager = SDMManager.getInstance();
        allCustomers = sdmManager.getCustomers();
        allCustomers.addListener(this);

        stores = sdmManager.getStores();
        for (Store store : stores) {
            store.addStoreChangeListener(this);
        }

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
        cell = new Cell[maxXValue+1][maxYValue+1];
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpFields();
    }

    private void setUpFields() {
        System.out.println("there are: " + customers.size() + " customers in system");
        System.out.println("there are: " + stores.size() + " stores in system");

        for (int i = maxXValue; i >= 0; i--){
            for (int j = 0; j <= maxYValue; j++){
                cell[i][j] = new Cell(i,j);
                pane.add(cell[i][j],j,maxXValue-i);
//                System.out.println("cell " + "(" + i + "," + j + ") was added to map!");
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
        //customers.clear();
        //stores.clear();
        setUpBasicData();
        setUpFields();
    }

    @Override
    public void onCustomersChange() {
        System.out.println("ViewMapController: Refreshing Map - on customer change");
        refresh();
    }

    @Override
    public void orderWasAdded(Store store, Order order) {
        System.out.println("ViewMapController: Refreshing Map - on order was added to store");
        refresh();
    }

    @Override
    public void itemPriceChanged(Store store, StoreItem item) {

    }

    @Override
    public void newStoreItemAdded(Store store, StoreItem item) {

    }

    @Override
    public void storeItemWasDeleted(Store store, StoreItem item) {

    }

    public void bindToMainAnchorPane(AnchorPane childAnchorPane) {
        mapAnchorPane.prefWidthProperty().bind(childAnchorPane.widthProperty());
        mapAnchorPane.prefHeightProperty().bind(childAnchorPane.heightProperty());
    }

    public class Cell extends Pane{
        private eMapElementType elementType;
        private String msg;
        private int x;
        private int y;


        public Cell(int i, int j) {
            setStyle("-fx-border-color: #828181");
            //this.setPrefSize(200,200);

            this.setMinSize(40,40);
            this.setMaxSize(40,40);


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


                storeIcon = new ImageView("/resources/map_icons/store.png");
                storeIcon.fitWidthProperty().bind(cell.widthProperty());
                storeIcon.fitHeightProperty().bind(cell.heightProperty());

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

                storeIcon = new ImageView("/resources/map_icons/customer.png");
                storeIcon.fitWidthProperty().bind(cell.widthProperty());
                storeIcon.fitHeightProperty().bind(cell.heightProperty());
                getChildren().add(storeIcon);
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
