package client;

import client.utils.Tray;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Main extends Application {

    static final Logger mainLogger = LogManager.getLogger(Main.class);

    public static Stage primaryStage;
    private static BorderPane rootLayout;

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
            mainLogger.error("initRootLayout_error", e);
        }
    }

    public static void showOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/client/fxml/LogonView.fxml"));
            AnchorPane overview = loader.load();
            rootLayout.setCenter(overview);
        } catch (IOException e) {
            mainLogger.error("showOverview_error", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Pocket desktop client");

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/client/images/icon.png")));

        //инициализируем главную сцену
        initRootLayout();

        //показываем общий вид
        showOverview();
        mainLogger.info("Окно приложения запущено");

        //значек в трее
        Tray tray = new Tray();
        tray.setTrayIcon();

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            Tray.trayON(primaryStage);
        });
    }
}
