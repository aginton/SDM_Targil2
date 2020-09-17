package components.ViewInfo.ViewMap;

import Logic.Customers.Customer;
import Logic.Store.Store;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import static components.ViewInfo.ViewMap.ViewMapController.TILE_SIZE;

public class Piece extends StackPane {
    private Object obj;
    private String basicInfo;

    public Piece(Object obj){
        StringBuilder sb = new StringBuilder();

        if (obj.getClass().equals(Store.class)){
            Store store = (Store)obj;
            System.out.println("I see you passed in a store!");
            sb.append("Store id: " + store.getStoreName())
                    .append("\nStore name: " + store.getStoreName())
                    .append("\nNumber of orders: " + store.getStoreOrders().size());
            basicInfo = sb.toString();

            Ellipse bg = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
            bg.setFill(Color.BLACK);

            bg.setStroke(Color.BLACK);
            bg.setStrokeWidth(TILE_SIZE * 0.03);

            getChildren().add(bg);

        } else if (obj.getClass().equals(Customer.class)){
            System.out.println("I see you passed a customer!");
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
