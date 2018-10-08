package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Pocket desktop Client");
        // курсор в виде руки на кнопках
        Scene scene = new Scene(root,750,400);
        Cursor cursor = Cursor.cursor("HAND");
        scene.setCursor(cursor);
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.setTitle("Закрывайте через кнопку Выход");
        });
        primaryStage.show();
    }

    public static void main(String[] args) {launch(args);}
}
