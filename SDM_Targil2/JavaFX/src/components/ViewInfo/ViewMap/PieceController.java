package components.ViewInfo.ViewMap;

import Logic.Customers.Customer;
import Logic.Store.Store;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import static components.ViewInfo.ViewMap.ViewMapController.TILE_SIZE;


public class PieceController {
    @FXML
    private StackPane stackpane;

    @FXML
    private ImageView image;

    private String basicInfo;

    public PieceController(Object obj){
        StringBuilder sb = new StringBuilder();

        if (obj.getClass().equals(Store.class)){
            Image imageObj = new Image("/resources/storecute.png");
            image = new ImageView(imageObj);
            Store store = (Store)obj;
            System.out.println("I see you passed in a store!");
            sb.append("Store id: " + store.getStoreName())
                    .append("\nStore name: " + store.getStoreName())
                    .append("\nNumber of orders: " + store.getStoreOrders().size());
            basicInfo = sb.toString();

        } else if (obj.getClass().equals(Customer.class)){
            System.out.println("I see you passed a customer!");
            Image imageObj = new Image("/resources/person.png");
            image = new ImageView(imageObj);

            Customer customer = (Customer) obj;
            System.out.println("I see you passed in a store!");
            sb.append("Store id: " + customer.getCustomerId())
                    .append("\nStore name: " + customer.getCustomerName())
                    .append("\nNumber of orders: " + customer.getOrders().size());
        } else{
            System.out.println("What the hell did you pass!?");
        }
    }

}
