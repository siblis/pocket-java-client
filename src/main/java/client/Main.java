package client;

import client.controller.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {launch(args);}

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Pocket desktop Client");

        //инициализируем главную сцену
        initRootLayout();

        //показываем общий вид
        showOverview();

//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setTitle("Pocket desktop Client");
//        // курсор в виде руки на кнопках
//        Scene scene = new Scene(root,750,400);
//        Cursor cursor = Cursor.cursor("HAND");
//        scene.setCursor(cursor);
//        primaryStage.setScene(scene);
//
//        primaryStage.setOnCloseRequest(event -> {
//            event.consume();
//            primaryStage.setTitle("Закрывайте через кнопку Выход");
//        });
//        primaryStage.show();
    }

    public void showOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("sample.fxml"));
            VBox overview = (VBox) loader.load();

            rootLayout.setCenter(overview);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);

            Cursor cursor = Cursor.cursor("HAND");
            scene.setCursor(cursor);
            primaryStage.setScene(scene);

            //RootLayoutController controller = loader.getController();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
