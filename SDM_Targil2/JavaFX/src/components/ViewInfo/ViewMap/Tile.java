package components.ViewInfo.ViewMap;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends StackPane {

    private Piece piece;
    private Rectangle rectangle;

    public Tile(boolean light, int x, int y){
        rectangle = new Rectangle();
        rectangle.setWidth(ViewMapController.WIDTH);
        rectangle.setHeight(ViewMapController.HEIGHT);

        relocate(x*ViewMapController.TILE_SIZE, y*ViewMapController.TILE_SIZE);
        rectangle.setFill(light? Color.AQUA: Color.CORAL);
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
}
