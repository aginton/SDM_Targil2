package components.ViewInfo.ViewMap;

import Logic.Customers.Customer;
import Logic.Store.Store;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Tile extends StackPane {

    private Piece piece;
    private Rectangle rectangle;

    public Tile(int x, int y){
        rectangle = new Rectangle();
        rectangle.setWidth(ViewMapController.WIDTH);
        rectangle.setHeight(ViewMapController.HEIGHT);

        relocate(x*ViewMapController.TILE_SIZE, y*ViewMapController.TILE_SIZE);
        rectangle.setFill(Color.CORAL);
        getChildren().add(rectangle);
    }

    public boolean hasPiece(){
        return piece!= null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public void setFill(Color color) {
        rectangle.setFill(color);
    }

    public void showInfo(){
        if (piece == null){
            System.out.println("Piece is empty!");
        } else{
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/components/PlaceAnOrder/SuccessOrError/SuccessPopUp.fxml"));
                Parent root = null;
                BasicInfoPopupController controller = loader.getController();
                root = loader.load();
                if (piece.getObj().getClass().equals(Store.class)){
                    Store store = (Store) piece.getObj();
                    controller.setStoreInfo(store);

                } else if (piece.getObj().getClass().equals(Customer.class)){
                    Customer customer = (Customer) piece.getObj();
                    controller.setCustomerInfo(customer);
                }

                Stage stage = new Stage();

                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
