package components.PlaceAnOrder.SuccessOrError;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class ConfirmBox {

    static boolean answer;

    public static boolean display(String title, String message){
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        Label label1 = new Label();
        label1.setText(message);

        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");

        yesButton.setOnAction(event -> {
            answer = true;
            window.close();
        });

        noButton.setOnAction(event -> {
            answer = false;
            window.close();
        });

        GridPane gridPane = new GridPane();
        gridPane.add(yesButton,1,1,1,1);
        gridPane.add(noButton,0,1,1,1);
        gridPane.add(label1, 0,0,2,1);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(gridPane);
        window.setScene(scene);
        window.showAndWait();
        return answer;
    }
}
