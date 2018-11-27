package client.view;

import client.Main;
import client.controller.ClientController;
import client.utils.Common;
import client.utils.CustomTextArea;
import client.utils.Sound;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static client.utils.Common.showAlert;

public class ChatViewController implements Initializable {

    private WebEngine webEngine;
    private String msgArea = "";

    @FXML
    private AnchorPane messagePanel;

    @FXML
    private WebView messageWebView;

    @FXML
    private ListView<String> contactListView;

    @FXML
    private CustomTextArea messageField;

    @FXML
    private TabPane tabContainer;

    @FXML
    private Tab chats;

    @FXML
    private Tab contacts;

    private ObservableList<String> contactsObservList;

    private ClientController clientController;

    private File chatBackgroundImage;

    public ChatViewController() {
    }

    public void setChatBackgroundImage(File fileName) {
        chatBackgroundImage = fileName;
    }

    public File getChatBackgroundImage() {
        return chatBackgroundImage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clientController = ClientController.getInstance();
        clientController.setChatViewController(this);
        contactsObservList = FXCollections.observableArrayList();
        fillContactListView();
        setChatBackgroundImage(new File(getClass().getResource("/client/images/chat-bg.jpg").getFile()));
        webtest();
        initFX(); //устанавливаем слушатель на обновление webView

        messageField.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode().equals(KeyCode.ENTER)) {
                String text = messageField.getText().trim();
                if (!text.isEmpty()) {
//                    messageField.appendText(System.lineSeparator());
                    clientController.sendMessage(messageField.getText());
                    messageField.clear();
                    messageField.requestFocus();
                }
                event.consume();
            }
        });
    }

    private void webtest() {
        webEngine = messageWebView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.loadContent("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "   <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "</head>\n" +
                "<body style=\"background-image: url(" + chatBackgroundImage.toURI().toString() + ")\">\n" +
                "   <div id=\"messageArea\">" +
                "   </div>\n" +
                "</body>\n" +
                "</html>");
    }

    public void fillContactListView() {
        contactListView.setItems(contactsObservList);
        contactListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            setText(item);
                            if (item.equals(clientController.getSenderName())) {
                                setStyle("-fx-font-weight: bold;" +
                                        " -fx-background-color: #ffead4");
                            }
                        } else {
                            setGraphic(null);
                            setText(null);
                        }
                    }
                };
            }
        });
        contactsObservList.clear();
        contactsObservList.addAll(clientController.getAllUserNames());
    }

    public void showMessage(String senderName, String message, Timestamp timestamp, boolean isNew) {
        if (isNew){
            Sound.playSound("src\\main\\resources\\client\\sounds\\1.wav").join();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        String formatSender = "<b><font color = " + (clientController.getSenderName().equals(senderName) ? "green" : "red") + ">"
                + senderName
                + "</font></b>";

        message = message.replaceAll("\n", "<br/>");
        message = Common.urlToHyperlink(message);

        msgArea += dateFormat.format(timestamp) + " " + formatSender + " " + message + "<br>";

        webEngine.loadContent("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "</head>\n" +

                "<body onload=\"pageScrollDown()\" style=\"background-image: url(" + getChatBackgroundImage().toURI().toString() + ")\">\n" +

                "        <div id=\"messageArea\">" +
                msgArea +
                "       </div>\n" +
                "<script language=\"javascript\" type=\"text/javascript\">\n" +
                "function pageScrollDown() {\n" +
                "document.body.scrollTop = document.body.scrollHeight;\n" +
                "}\n" +
                "</script>\n" +
                "    </body>\n" +
                "</html>");
    }

    @FXML
    private void handleDisconnectButton() {
        Stage stage = (Stage) messagePanel.getScene().getWindow();
        stage.close();
        Main.initRootLayout();
        Main.showOverview();
    }

    @FXML
    private void handleExit() {
        clientController.dbServiceClose();
        clientController.disconnect();
        System.exit(0);
    }

    @FXML
    private void handleSendMessage() {
        if (!messageField.getText().isEmpty()) {
            clientController.sendMessage(messageField.getText());
            messageField.clear();
            messageField.requestFocus();
        }
    }

    @FXML
    private void handleClientChoice(MouseEvent event) {
        if (event.getClickCount() == 1) {
            String receiver = contactListView.getSelectionModel().getSelectedItem();
            //showAlert("Сообщения будут отправляться контакту " + receiver, Alert.AlertType.INFORMATION);
            clientController.setReceiver(receiver);
        }

        messageField.requestFocus();
        messageField.selectEnd();
    }

    @FXML
    private void handleAddContactButton() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/AddContactView.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add contact");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void initFX() {
        final String EVENT_TYPE_CLICK = "click";

        //отслеживаем изменения в messageWebView
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                //messageWebView получил новое состояние, страница загружена полностью
                if (newState == Worker.State.SUCCEEDED) {
                    EventListener listener = new EventListener() {
                        @Override
                        public void handleEvent(Event ev) {
                            String domEventType = ev.getType();
                            System.err.println("EventType: " + domEventType); // DEBUG
                            if (domEventType.equals(EVENT_TYPE_CLICK)) {
                                String href = ((Element) ev.getTarget()).getAttribute("href");
                                System.out.println("href: " + href); // DEBUG
                                try {
                                    // Open URL in Browser:
                                    Desktop desktop = Desktop.getDesktop();
                                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                                        desktop.browse(new URI(href.contains("://") ? href : "http://" + href + "/"));
                                        //отменяем событие, чтобы ссылка не открывалась в самом webView
                                        ev.preventDefault();
                                    } else {
                                        System.out.println("Could not load URL: " + href);
                                    }
                                    System.out.println("Opening external browser.");
                                } catch (IOException | URISyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };

                    Document doc = webEngine.getDocument();
                    NodeList nodeList = doc.getElementsByTagName("a");
                    System.out.println(nodeList.getLength());

                    for (int i = 0; i < nodeList.getLength(); i++) {
                        //сначало удаляем ранее установленные слушатели
                        ((EventTarget) nodeList.item(i)).removeEventListener(EVENT_TYPE_CLICK, listener, false);
                        ((EventTarget) nodeList.item(i)).addEventListener(EVENT_TYPE_CLICK, listener, false);
                        System.out.println("Remove & after add event listener. " + nodeList.item(i));
                    }
                }
            }
        });
    }
    //метод выбора файла
    private Desktop desktop = Desktop.getDesktop();
    @FXML
    public void handleSendFile() {
        Stage stage = (Stage) messagePanel.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                this.desktop.open(file);//открывается файл на компьютере
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<File> files = Arrays.asList(file);
            if (files == null || files.isEmpty()) return;
            for(File f : files) {
                messageField .appendText(f.getAbsolutePath() + "\n");
            }
        }
    }
    //метод добавления смайликов
    public void handleSendSmile(MouseEvent mouseEvent) {
    }

    public void clearMessageWebView() {
        msgArea = "";
        webEngine.loadContent("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "</head>\n" +

                "<body style=\"background-image: url(" + getChatBackgroundImage().toURI().toString() + ")\">\n" +

                "</body>\n" +
                "</html>");
    }

    //метод смены иконки
    public void handleOnChatSelected() {
        chats.setGraphic(buildImage("/client/images/chat/chatsActive.png"));
        if (contacts != null) {
            contacts.setGraphic(buildImage("/client/images/chat/contacts.png"));
            contacts.setStyle("-fx-border-width: 0 0 5 0; " +
                    "          -fx-border-color: #3498DB #3498DB transparent #3498DB;" +
                    "-fx-border-insets: 0;" +
                    "          -fx-border-style: solid;");
        }
        chats.setStyle("-fx-border-width: 0 0 5 0; " +
                        "-fx-border-color: transparent transparent #F8D57D transparent;" +
                "-fx-border-insets: 0;" +
                        "-fx-border-style: solid;");
    }
    public void handleOnContactSelected() {
        contacts.setGraphic(buildImage("/client/images/chat/contactsActive.png"));
        chats.setGraphic(buildImage("/client/images/chat/chats.png"));
        contacts.setStyle("-fx-border-width: 0 0 5 0; " +
                "-fx-border-color: transparent transparent #F8D57D transparent;" +
                "-fx-border-insets: 0;" +
                "-fx-border-style: solid;");
        chats.setStyle("-fx-border-width: 0 0 5 0; " +
                "       -fx-border-color: #3498DB #3498DB transparent #3498DB;" +
                "-fx-border-insets: 0;" +
                "       -fx-border-style: solid;");
    }

    private ImageView buildImage(String s) {
        Image i = new Image(s);
        ImageView imageView = new ImageView();
        imageView.setImage(i);
        return imageView;
    }

}
