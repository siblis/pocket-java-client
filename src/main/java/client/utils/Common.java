package client.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String urlToHyperlink(String inputString) {
        String s = "(?i)(?:(?:https?|ftp)://)?(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?";
        StringBuffer resultString = new StringBuffer();
        Pattern regex = Pattern.compile(s);
        Matcher m = regex.matcher(inputString);
        while (m.find()) {
            String replacement = m.group(0).replaceAll(m.group(0), "<a href=\"" + m.group(0) + "\">" + m.group(0) + "</a>");
            m.appendReplacement(resultString, replacement);
        }
        m.appendTail(resultString);
        return resultString.toString();
    }
}
