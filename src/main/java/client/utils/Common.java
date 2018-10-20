package client.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class Common {

    public static void showAlert(String message, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            String title = null;
            switch (alertType) {
                case INFORMATION:
                    title = "Информация";
                break;
                case CONFIRMATION:
                    title = "Подтверждение";
                break;
                case ERROR:
                    title = "Ошибка";
                break;
                default:
                    break;
            }
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
