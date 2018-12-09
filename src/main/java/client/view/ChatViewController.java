package client.view;

import client.Main;
import client.controller.ClientController;
import client.utils.Common;
import client.utils.CustomTextArea;
import client.utils.Sound;
import client.view.customFX.CFXListElement;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import javax.swing.*;
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
    private JFXListView<CFXListElement> contactListView;

    @FXML
    private CustomTextArea messageField;

    @FXML
    private TabPane tabContainer;

    @FXML
    private Tab chats;

    @FXML
    private Tab contacts;

    private ObservableList<CFXListElement> contactsObservList;

    private ClientController clientController;

    //private File chatBackgroundImage;
    private String backgroundImage;

    private Document DOMdocument;

    private String tsOld;

    ////////////////////////

    public ChatViewController() {
    }

    /*private void setChatBackgroundImage(File fileName) {
        chatBackgroundImage = fileName;
    }

    private File getChatBackgroundImage() {
        return chatBackgroundImage;
    }*/

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DOMdocument = null;
        tsOld = null;

        webEngine = messageWebView.getEngine(); //инициализация WebEngine
        initBackgroundWebView();
        //initWebView(); //при запуске от теста вызывается еще раз. Если не будет вызова там, тут расскоментировать

        clientController = ClientController.getInstance();
        clientController.setChatViewController(this);
        contactsObservList = FXCollections.observableArrayList();
        contactListView.setExpanded(true);
        fillContactListView();

        //webtest();

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

    /*private void webtest() {
        webEngine = messageWebView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.loadContent("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "   <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "</head>\n" +
                //"<body style=\"background-image: url(" + chatBackgroundImage.toURI().toString() + ")\">\n" +
                "<body style=\"background-image: url(" + backgroundImage + ")\">\n" +
                "   <div id=\"messageArea\">" +
                "   </div>\n" +
                "</body>\n" +
                "</html>");
    }*/
    //private
    //  инициализация картинки backgrounda
    private void initBackgroundWebView() {
        String path = "client/images/chat-bg.jpg"; //картинка фона
        ClassLoader cl = this.getClass().getClassLoader();
        backgroundImage = "";
        try {
            backgroundImage = cl.getResource(path).toURI().toString();
        }catch (Exception e) {
            //todo перенести в логирование
            e.printStackTrace();
        }
    }

    // инициализация только HTML в WebView.
    private void initWebView() {
        //webEngine.setJavaScriptEnabled(true);
        webEngine.loadContent(
                "<!DOCTYPE html> \n"+
                "<html lang=\"en\"> \n"+
                  "<head> \n"+
                    "<meta charset=UTF-8> \n"+
                    "<style> \n"+
                        "body { \n" +
                            "margin: 0; \n"+
                            "padding: 0; \n"+
                            "background-image: url(" + backgroundImage + "); \n"+
                            "background-attachment: fixed; \n"+
                        "} \n"+
                        //общие стили
                        //time day
                        ".timeStampDay { \n" +
                            "display: inline-block; \n"+
                            "text-align: center; \n"+
                            "width: 80px; \n"+
                            "margin: 0 38%;  \n"+
                            "color: #55635A; \n"+
                            "background: #BCDCC9; \n"+
                            "border-radius: 10px; \n"+
                            "padding: 5px 10px; \n"+
                        "} \n"+
                        //
                        ".message { \n"+
                            "display: flex; \n"+
                            //"height: auto; \n"+
                            //"width: 90%; \n"+
                            "align-items: center; \n"+
                            "margin-left: 10px; \n"+
                            "margin-right: 10px; \n"+
                            "margin-top: 10px; \n"+
                            "margin-bottom: 30px; \n"+
                        "} \n"+
                        //div Logo
                        ".msgLogo { \n"+
                            "flex: none; \n"+
                            "align-self: start; \n"+
                            "width: 35px; \n"+
                            "height: 35px; \n"+
                            "background: lightgrey; \n"+
                            "border-radius: 50%; \n"+
                        "} \n"+
                        //div text, 1->2
                        ".msgTxt { \n"+
                            "display: flex \n"+
                            "flex-direction: column; \n"+
                            "flex: auto; \n"+
                            "min-width: 200px; \n"+
                            "border-radius: 15px; \n"+
                            "margin-left: 10px; \n"+
                            "margin-right: 10px; \n"+
                            "padding: 10px; \n"+
                            "box-shadow: -1px 1px 2px 2px #DCD8D3; \n"+
                        "} \n"+
                        //div time
                        ".msgTime { \n"+
                            "flex: auto; \n"+
                        "} \n"+

                        //div msgTxt --> sender
                        ".myUserClass { \n"+
                            "background: #C6FCFF; \n"+
                        "} \n"+"" +
                        ".senderUserClass { \n"+
                            "background: #FFFFFF; \n"+
                        "} \n"+

                        //div text --> div sender
                        ".myUserClassS{ \n"+
                            "display: none; \n"+ //Отправителя себя не отображаем
                        "} \n"+

                        ".senderUserClassS{ \n"+
                            "word-wrap: break-word; \n"+    //<!--Перенос слов-->
                            "color: #1EA362; \n"+
                        "} \n"+

                        //div text --> div msg
                        ".msg { \n"+
                            "width: auto; \n"+
                            "word-wrap: break-word; \n"+    //<!--Перенос слов-->
                        "} \n"+

                        //div time -->sender
                        ".myUserClassT { \n"+
                            "color: #6399F3; \n"+
                        "} \n"+
                        ".senderUserClassT { \n"+
                            "color: #959493; \n"+
                        "} \n"+
                    "</style> \n"+
                  "</head> \n"+
                  "<body></body> \n"+
                  /*"<script> \n"+
                    "document.body.onload=pageScrollDown; \n"+
                    "function pageScrollDown() { \n"+
                        "document.body.scrollTop = document.body.scrollHeight; \n"+
                    "} \n"+
                  "</script> \n"+*/
                "</html> \n");
    }

    public void fillContactListView() {
        contactListView.setItems(contactsObservList);
//        contactListView.setCellFactory(new Callback<ListView<CFXListElement>, ListCell<CFXListElement>>() {
//
//            @Override
//            public ListCell<CFXListElement> call(ListView<CFXListElement> param) {
//                return new ListCell<CFXListElement>() {
//                    @Override
//                    protected void updateItem(CFXListElement item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (!empty) {
//
//                            setText(item.getTopic());
//                            if (item.equals(clientController.getSenderName())) {
//                                setStyle("-fx-font-weight: bold;" +
//                                        " -fx-background-color: #ffead4");
//                            }
//                        } else {
//                            setGraphic(null);
//                            setText(null);
//                        }
//                    }
//                };
//            }
//        });
      //  contactsObservList.clear();
        contactsObservList.addAll(clientController.getContactListOfCards());
        for (CFXListElement element:contactsObservList){
            element.setUnreadMessages("0");
            element.setBody("Входящие сообщения");

        }
    }

    public void showMessage(String senderName, String message, Timestamp timestamp, boolean isNew) {
        if (isNew) {
            String path = "client/sounds/1.wav"; //звук нового сообщения
            ClassLoader cl = this.getClass().getClassLoader();
            try {

                URL soundMsg = cl.getResource(path);
                Sound.playSound(soundMsg).join();

            } catch (Exception e) { //todo поправить Exception, тут их всего 2
                //todo перенести в логирование
                e.printStackTrace();
            }
        }
        /*if (isNew){
            Sound.playSound("src\\main\\resources\\client\\sounds\\1.wav").join();
        }*/
        SimpleDateFormat dateFormatDay = new SimpleDateFormat("d MMMM");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        /*String formatSender = "<b><font color = " + (clientController.getSenderName().equals(senderName) ? "green" : "red") + ">"
                + senderName
                + "</font></b>";

        message = message.replaceAll("\n", "<br/>");
        message = Common.urlToHyperlink(message);

        msgArea += dateFormat.format(timestamp) + " " + formatSender + " " + message + "<br>";*/

        //Node body = webEngine.getDocument().getElementsByTagName("body").item(0);

        //Подбор наше сообщение или чужое. Такой класс DIV и будем ставить
        String attrClass="";
        if (clientController.getSenderName().equals(senderName)) {
            attrClass = "myUserClass";
        } else {
            attrClass = "senderUserClass";
        }
        boolean visibleDateDay=false;

        if (tsOld == null) {
            tsOld = dateFormatDay.format(timestamp);
            visibleDateDay = true;
        }else if (!tsOld.equals(dateFormatDay.format(timestamp))) {
            tsOld = dateFormatDay.format(timestamp);
            visibleDateDay = true;
        }

        //Подписка на событие загрузки документа HTML in WebView
        if (DOMdocument == null) {
            String attrClass2 = attrClass; //не понял почему, но attrClass требуется final не изменяемый дальше
            Boolean visibleDateDay2 = visibleDateDay;
            webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    DOMdocument = webEngine.getDocument();
                    Node body = DOMdocument.getElementsByTagName("body").item(0);
                    // todo structure module message
                    /* Create module DIV for messenger
                    <div class="timeStampDay"></div>
                    <div class="message">
                        <div class="msgLogo"></div>
                        <div class="attrClass msgTxt">
                            <div class="'attrClass+S' sender"></div>
                            <div class="'attrClass+M' msg"></div>
                        </div>
                        <div class="'attrClass+T' msgTime"></div>
                    </div>
                    Style create in initWebView
                     */
                    if (visibleDateDay2) {
                        Element divTimeDay = webEngine.getDocument().createElement("div");
                        divTimeDay.setAttribute("class", "timeStampDay");
                        divTimeDay.setTextContent(dateFormatDay.format(timestamp));
                        body.appendChild(divTimeDay);
                    }
                    Element div = webEngine.getDocument().createElement("div");
                    Element divLogo = webEngine.getDocument().createElement("div");
                    Element divTxt = webEngine.getDocument().createElement("div");
                    Element divTxtSender = webEngine.getDocument().createElement("div");
                    Element divTxtMsg = webEngine.getDocument().createElement("div");
                    Element divTime = webEngine.getDocument().createElement("div");
                    div.setAttribute("class", "message");
                    divLogo.setAttribute("class", "msgLogo");
                    divTxt.setAttribute("class", attrClass2+" msgTxt");
                    divTxtSender.setAttribute("class", attrClass2+"S sender");
                    divTxtMsg.setAttribute("class", attrClass2+"M msg");
                    divTime.setAttribute("class", attrClass2+"T msgTime");
                    divTxtSender.setTextContent(senderName);
                    divTxtMsg.setTextContent(message);
                    divTime.setTextContent(dateFormat.format(timestamp));
                    div.appendChild(divLogo);
                    divTxt.appendChild(divTxtSender);
                    divTxt.appendChild(divTxtMsg);
                    div.appendChild(divTxt);
                    div.appendChild(divTime);
                    body.appendChild(div);
                }
            });
        }else {
            Node body = DOMdocument.getElementsByTagName("body").item(0);
            if (visibleDateDay) {
                Element divTimeDay = webEngine.getDocument().createElement("div");
                divTimeDay.setAttribute("class", "timeStampDay");
                divTimeDay.setTextContent(dateFormatDay.format(timestamp));
                body.appendChild(divTimeDay);
            }
            Element div = webEngine.getDocument().createElement("div");
            Element divLogo = webEngine.getDocument().createElement("div");
            Element divTxt = webEngine.getDocument().createElement("div");
            Element divTxtSender = webEngine.getDocument().createElement("div");
            Element divTxtMsg = webEngine.getDocument().createElement("div");
            Element divTime = webEngine.getDocument().createElement("div");
            div.setAttribute("class", "message");
            divLogo.setAttribute("class", "msgLogo");
            divTxt.setAttribute("class", attrClass+" msgTxt");
            divTxtSender.setAttribute("class", attrClass+"S sender");
            divTxtMsg.setAttribute("class", attrClass+"M msg");
            divTime.setAttribute("class", attrClass+"T msgTime");
            divTxtSender.setTextContent(senderName);
            divTxtMsg.setTextContent(message);
            divTime.setTextContent(dateFormat.format(timestamp));
            div.appendChild(divLogo);
            divTxt.appendChild(divTxtSender);
            divTxt.appendChild(divTxtMsg);
            div.appendChild(divTxt);
            div.appendChild(divTime);
            body.appendChild(div);
        }

       /*webEngine.loadContent("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "   <style> \n" +
                "       body { \n" +
                //"           background-image: url(" + getChatBackgroundImage().toURI().toString() + "); \n" +
                "           background-image: url(" + backgroundImage + "); \n" +
                "           background-attachment: fixed; \n" +
                "       } \n" +
                "       #messageArea {\n"+
                "           word-wrap: break-word; \n" + //Перенос слов
                "       }\n"+
                "   </style> \n" +
                "</head>\n" +

                //ШПС 181202 - Перенес стили вверх, в отдельный тег
               // "<body onload=\"pageScrollDown()\" style=\"background-image: url(" + getChatBackgroundImage().toURI().toString() + "); background-attachment: fixed;\">\n" +
                "<body onload=\"pageScrollDown()\"> \n" +

                "        <div id=\"messageArea\">" +
                msgArea +
                "       </div>\n" +
                "<script language=\"javascript\" type=\"text/javascript\">\n" +
                "function pageScrollDown() {\n" +

                "document.body.scrollTop = document.body.scrollHeight;\n" +
                "}\n" +
                "</script>\n" +
                "    </body>\n" +
                "</html>");*/
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
            String receiver = contactListView.getSelectionModel().getSelectedItem().getTopic();
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
        //msgArea = "";
        initWebView();
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
