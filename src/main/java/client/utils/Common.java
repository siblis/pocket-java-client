package client.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Common {

    private static final Logger logger = LogManager.getLogger(Common.class.getName());

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
        try {
            //шаблон гиперссылки
            String s = "(?i)(?:(?:https?|ftp)://)?(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?";
            StringBuffer resultString = new StringBuffer();
            Pattern regex = Pattern.compile(s);
            Matcher m = regex.matcher(inputString);
            while (m.find()) {
                String replacement;
                String hyperlink = m.group(0);
                //шаблон для проверки окончания строки на наличие расширения файла
                //Pattern p = Pattern.compile("(?i).+\\.(jpg|jpeg|png)");
                Pattern p = Pattern.compile(".+\\.(jpg|jpeg|jpe|jpg2000|bmp|bitmap|png|gif|ecw|ico|pcx|tga|xps|psd|pds|tiff|tif|wdp|hdp|cpt|ai|cdr|cgm|svg|wmf|emf|eps|ps|xar)$",
                        Pattern.CASE_INSENSITIVE);
                Matcher mm = p.matcher(hyperlink);
                if (mm.matches()) {
                    replacement = m.group(0).replaceAll(m.group(0),
                            "<a href=\"" + m.group(0) + "\">" +
                                    "<img src=\"" + m.group(0) + "\" " +
                                    "alt=\"" + m.group(0) + "\" " +
                                    "title=\"" + m.group(0) + "\" " +
                                    //                                "width=\"300\" " +
                                    "width=\"auto\" height=\"300\" " +
                                    "min-width=\"100\" min-height=\"100\" " +
                                    "max-width=\"300\" max-height=\"300\" " +
                                    "srcset=\"" +
                                    m.group(0) + " 320w," +
                                    m.group(0) + " 480w," +
                                    m.group(0) + " 768w," +
                                    m.group(0) + " 1024w," +
                                    m.group(0) + " 1280w\" " +
                                    "sizes=\"" +
                                    "(min-width: 100) 20vw," +
                                    "(max-width: 700) 50vw\">" +
                                    "</a>");
                } else {
                    replacement = m.group(0).replaceAll(m.group(0), "<a href=\"" + m.group(0) + "\">" + m.group(0) + "</a>");
                }
                m.appendReplacement(resultString, replacement);
            }
            m.appendTail(resultString);
            return resultString.toString();
        } catch (PatternSyntaxException pse) {
            System.err.println("Неправильное регулярное выражение: " + pse.getMessage());
            System.err.println("Описание: " + pse.getDescription());
            System.err.println("Позиция: " + pse.getIndex());
            System.err.println("Неправильный шаблон: " + pse.getPattern());
           /* FIXME Я уверен что здесь нужно писать подругому и скорее всего System.err.println можно убирать.*/
            logger.info( "Неправильное регулярное выражение: " + pse.getMessage() + "\n" + "Описание: " + pse.getDescription() + "\n" + "Позиция: " + pse.getIndex() + "\n" +"Неправильный шаблон: " + pse.getPattern()+ "\n");
        } catch (Exception e) {
            System.err.println("Ошибка при поиске регулярного выражения");
            e.printStackTrace();
            logger.info("Ошибка при поиске регулярного выражения", e);
        }
        return inputString;
    }
}
