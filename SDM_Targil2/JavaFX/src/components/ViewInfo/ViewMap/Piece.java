package components.ViewInfo.ViewMap;

import Logic.Customers.Customer;
import Logic.Store.Store;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static components.ViewInfo.ViewMap.ViewMapController.TILE_SIZE;

public class Piece extends StackPane {
    private Object obj;
    private Class clazz;
    private String basicInfo;
    private ImageView image;

    public Piece(Customer customer){
        StringBuilder sb = new StringBuilder();
        clazz = Customer.class;
        obj = customer;

        System.out.println("I see you passed a customer!");
        Image imageObj = new Image("/resources/person.png");
        clazz = Customer.class;
        image = new ImageView(imageObj);
        image.setFitHeight(ViewMapController.TILE_SIZE/2);
        image.setFitWidth(ViewMapController.TILE_SIZE/2);


        System.out.println("I see you passed in a store!");
        sb.append("Store id: " + customer.getCustomerId())
                .append("\nStore name: " + customer.getCustomerName())
                .append("\nNumber of orders: " + customer.getOrders().size());

        getChildren().add(image);
    }

    public Piece(Store store){
        clazz = Store.class;
        Image imageObj = new Image("/resources/storecute.jpg");
        image = new ImageView(imageObj);
        obj = store;

        StringBuilder sb = new StringBuilder();
        image.setFitHeight(ViewMapController.TILE_SIZE/2);
        image.setFitWidth(ViewMapController.TILE_SIZE/2);
        System.out.println("I see you passed in a store!");
        sb.append("Store id: " + store.getStoreName())
                .append("\nStore name: " + store.getStoreName())
                .append("\nNumber of orders: " + store.getStoreOrders().size());
        basicInfo = sb.toString();
        getChildren().add(image);
    }


    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(String basicInfo) {
        this.basicInfo = basicInfo;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }


    public void showInfo() {
        try {
            System.out.println("Yanna dabba");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/components/ViewInfo/ViewMap/BasicInfoPopup.fxml"));
            Parent root = loader.load();
            BasicInfoPopupController controller = loader.getController();
            if (clazz.equals(Store.class)){
                Store store = (Store) obj;
                controller.setStoreInfo(store);

            } else if (clazz.equals(Customer.class)){
                Customer customer = (Customer) obj;
                controller.setCustomerInfo(customer);
            }

            System.out.println(basicInfo);
            Stage stage = new Stage();

            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
