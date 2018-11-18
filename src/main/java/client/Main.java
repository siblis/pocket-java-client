package client;

import client.utils.Common;
import client.view.Tray;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    private static Stage primaryStage;
    private static BorderPane rootLayout;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Pocket desktop client");

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/client/images/icon.png")));

        //инициализируем главную сцену
        initRootLayout();

        //показываем общий вид
        showOverview();

        //значек в трее
        Tray tray = new Tray();
        try {
            tray.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            //primaryStage.setTitle("Закрывайте через кнопку Выход");
            Tray.trayON(primaryStage);
            //Common.showAlert("Закрывайте через кнопку Выход", Alert.AlertType.ERROR);
        });
    }

    public static void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/client/fxml/RootLayout.fxml"));
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);

            Cursor cursor = Cursor.cursor("HAND");
            scene.setCursor(cursor);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/client/fxml/LogonView.fxml"));
            AnchorPane overview = loader.load();
            rootLayout.setCenter(overview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
