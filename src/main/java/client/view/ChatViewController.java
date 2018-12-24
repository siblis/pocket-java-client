package client.view;

import client.Main;
import client.controller.ClientController;
import client.utils.Common;
import client.utils.CustomTextArea;
import client.utils.Sound;
import client.view.customFX.CFXListElement;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import java.awt.*;
import java.awt.Label;
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

    @FXML
    private AnchorPane messagePanel;

    @FXML
    private WebView messageWebView;

    @FXML
    private JFXListView<CFXListElement> contactListView;

    @FXML
    private CustomTextArea messageField;

    @FXML
    private Tab chats;

    @FXML
    private Tab contacts;

    @FXML
    private AnchorPane userSearchPane;

    @FXML
    private JFXButton bAddContact;

    @FXML
    private AnchorPane groupSearchPane;

    @FXML
    private AnchorPane groupListPane;

    @FXML
    private AnchorPane groupNewPane;

    @FXML
    private JFXListView<CFXListElement> listViewAddToGroup;


    //
    private WebEngine webEngine;

    private ObservableList<CFXListElement> contactsObservList;

    private ClientController clientController;

    private String backgroundImage;

    private Document DOMdocument;

    private String tsOld;

    private int idDivMsg;

    //ссылка на desktop
    private Desktop desktop;
    ////////////////////////

    public ChatViewController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DOMdocument = null;
        tsOld = null; //чистка даты
        idDivMsg = 0; //присваивание ID

        webEngine = messageWebView.getEngine(); //инициализация WebEngine
        initBackgroundWebView();
        initWebView();

        clientController = ClientController.getInstance();
        clientController.setChatViewController(this);
        contactsObservList = FXCollections.observableList(ClientController.getInstance().getContactListOfCards());
        contactListView.setExpanded(true);
        fillContactListView();

        desktop = Desktop.getDesktop();

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
        webEngine.loadContent(
                "<!DOCTYPE html> \n"+
                "<html lang=\"en\"> \n"+
                  "<head> \n"+
                    "<meta charset=UTF-8> \n"+
                    "<style> \n"+
                        "body { \n" +
                            "margin: 0; \n"+
                            "padding: 10px; \n"+
                            "background-image: url(" + backgroundImage + "); \n"+
                            "background-attachment: fixed; \n"+
                        "} \n"+
                        //общие стили
                        //time day
                        ".timeStampDay { \n" +
                            "display: inline-block; \n"+
                            "text-align: center; \n"+
                            //"width: 80px; \n"+
                            "margin: 0 38%;  \n"+
                            "margin-top: 10px;  \n"+
                            "color: #55635A; \n"+
                            "background: #BCDCC9; \n"+
                            "border-radius: 10px; \n"+
                            "padding: 5px 10px; \n"+
                        "} \n"+
                        //
                        ".message { \n"+
                            "display: flex; \n"+
                            "width: 0px; \n"+
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
                            "max-width: 400px; \n"+
                            "min-width: 200px; \n"+
                            "width: 300px; \n"+
                            "border-radius: 20px; \n"+
                            "margin-left: 10px; \n"+
                            "margin-right: 10px; \n"+
                            "padding: 16px; \n"+
                            "box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.15); \n"+
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
                            "color: #757575; \n"+
                        "} \n"+
                        ".senderUserClassT { \n"+
                            "color: #4285F4; \n"+
                        "} \n"+
                    "</style> \n"+
                  "</head> \n"+
                  "<body></body> \n"+
                "</html> \n");
    }

    public void fillContactListView() {
        contactListView.setItems(contactsObservList);
        //contactsObservList.addAll(clientController.getContactListOfCards());
        for (CFXListElement element:contactsObservList){
            element.setUnreadMessages("0");
            element.setBody("Входящие сообщения");

        }
    }

    /**
     *
     * @param pattern
     * @return
     * Устанавливаем формат даты
     */
    private SimpleDateFormat initDateFormat(String pattern){
        return new SimpleDateFormat(pattern);
    }

    /**
     *
     * @param message
     * @param senderName
     * @param timestamp     *
     * @param attrClass
     * ****
     * /* Create module DIV for messenger
     * <div class="timeStampDay"></div>
         * <div class="message">
             * <div class="msgLogo"></div>
             * <div class="attrClass msgTxt">
     *          <div class="'attrClass+S' sender"></div>
     *          <div class="'attrClass+M' msg">
     *              <Если ссылка то
     *              <a href=ссылка></a>
     *          </div>
     *        </div>
         * </div>
         * <div class="'attrClass+T' msgTime"></div>
     * </div>
     * Style create in initWebView
     *
     */
    private void createMessageDiv(String message, String senderName, Timestamp timestamp, String attrClass){
        //ID требуется для скрипта вставки тегов
        idDivMsg+=1;
        String idMsg = "msg"+idDivMsg;

        SimpleDateFormat dateFormatDay = initDateFormat("d MMMM");
        SimpleDateFormat dateFormat = initDateFormat("HH:mm");

        //Заменяем Enter на перенос строки, для отображения
        message = message.replaceAll("\n", "<br/>");
        //Парсим ссылки, получаем строку вида <a href="message">message</a>
        message = Common.urlToHyperlink(message);

        boolean visibleDateDay=false;
        if (tsOld == null) {
            tsOld = dateFormatDay.format(timestamp);
            visibleDateDay = true;
        }else if (!tsOld.equals(dateFormatDay.format(timestamp))) {
            tsOld = dateFormatDay.format(timestamp);
            visibleDateDay = true;
        }

        Node body = DOMdocument.getElementsByTagName("body").item(0);

        if (visibleDateDay) {
            Element divTimeDay = DOMdocument.createElement("div");
            divTimeDay.setAttribute("class", "timeStampDay");
            divTimeDay.setTextContent(dateFormatDay.format(timestamp));
            body.appendChild(divTimeDay);
        }
        Element div = DOMdocument.createElement("div");
        Element divLogo = DOMdocument.createElement("div");
        Element divTxt = DOMdocument.createElement("div");
        Element divTxtSender = DOMdocument.createElement("div");
        Element divTxtMsg = DOMdocument.createElement("div");
        Element divTime = DOMdocument.createElement("div");
        div.setAttribute("class", "message");
        divLogo.setAttribute("class", "msgLogo");
        divTxt.setAttribute("class", attrClass+" msgTxt");
        divTxtSender.setAttribute("class", attrClass+"S sender");
        divTxtMsg.setAttribute("class", attrClass+"M msg");
        divTxtMsg.setAttribute("id", idMsg); //id
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
        //Scripts
        //вставляем текст с тегами
        webEngine.executeScript("document.getElementById(\"" + idMsg + "\").innerHTML = '" + message+"'");
        //Сдвигаем страницу на последний элемент
        webEngine.executeScript("document.body.scrollTop = document.body.scrollHeight");
        //Подписка на событие по открытию ссылки
        addListenerLinkExternalBrowser(divTxtMsg);
    }

    public void showMessage(String senderName, String message, Timestamp timestamp, boolean isNew) {
        if (isNew) {
            Sound.playSoundNewMessage().join();
        }

        String attrClass="";
        if (clientController.getSenderName().equals(senderName)) {
            attrClass = "myUserClass";
        } else {
            attrClass = "senderUserClass";
        }

        //Подписка на событие загрузки документа HTML in WebView
        if (DOMdocument == null) {
            String attrClass2 = attrClass; //не понял почему, но attrClass требуется final не изменяемый дальше
            webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    DOMdocument = webEngine.getDocument();
                    createMessageDiv(message, senderName, timestamp, attrClass2);
                }
            });
        }else {
            createMessageDiv(message, senderName, timestamp, attrClass);
        }
    }

    @FXML
    private void handleDisconnectButton() {
        Stage stage = (Stage) messagePanel.getScene().getWindow();
        stage.close();
        clientController.disconnect();
        Tray.currentStage = null;
        Main.initRootLayout();
        Main.showOverview();
    }

    @FXML
    private void handleExit() {
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
        contactListView.setVisible(false);
        bAddContact.setVisible(false);
        userSearchPane.setVisible(true);
        userSearchPane.setFocusTraversable(true);

//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/AddContactView.fxml"));
//        Parent root = fxmlLoader.load();
//        Stage stage = new Stage();
//        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.setTitle("Add contact");
//        stage.setResizable(false);
//        stage.setScene(new Scene(root));
//        stage.show();

    }
    @FXML
    private void onUserSearchButtonClicked(){
        bAddContact.setVisible(true);
        contactListView.setVisible(true);
        userSearchPane.setVisible(false);
    }

    //подписка на обработку открытия ссылок
    //Element tagElement = <div class="msg">
    private void addListenerLinkExternalBrowser(Element tagElement){
        NodeList nodeList = tagElement.getElementsByTagName("a");
        for (int i = 0; i < nodeList.getLength(); i++) {
            ((EventTarget) nodeList.item(i)).addEventListener("click", listenerLinkExternalBrowser(), false);

        }
    }

    //обработчик открытия ссылок во внешнем браузере
    private EventListener listenerLinkExternalBrowser(){
        EventListener listener = new EventListener() {

            @Override
            public void handleEvent(Event evt) {
                String domEventType = evt.getType();
                if ("click".equals(domEventType)) {
                    String href = ((Element) evt.getTarget()).getAttribute("href");
                    try {
                        // Open URL in Browser:
                        //ну удалил, т.к. не много не понятно пока зачем
                        //if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            desktop.browse(new URI(href.contains("://") ? href : "http://" + href + "/"));
                            //отменяем событие, чтобы ссылка не открывалась в самом webView
                            evt.preventDefault();
                        /*} else {
                            System.out.println("Could not load URL: " + href);
                        }*/
                    } catch (IOException | URISyntaxException e) {
                        //todo logger
                        e.printStackTrace();
                    }
                }
            }
        };
        return listener;
    }

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

    /**
     * Вызывается для чистки документа внутри WebEngine
     * при первом вызове чистки нет, т.к. DOMdocument == null
     * так де обнуляем дату для группировки (tsOld) и ID для DIV
     */
    public void clearMessageWebView() {
        if (DOMdocument != null) {
            //чистим все, что внутри тегов <body></body>
            Node body = DOMdocument.getElementsByTagName("body").item(0);
            Node fc = body.getFirstChild();
            while (fc != null) {
                body.removeChild(fc);
                fc = body.getFirstChild();
            }
        }

        tsOld = null; //чистка даты
        idDivMsg =0; //присваивание ID
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

    public void onGroupSearchButtonClicked(ActionEvent actionEvent) {
        groupSearchPane.setVisible(false);
    }

    public void handleGroupSearchButton(MouseEvent mouseEvent) {
        groupListPane.setVisible(false);
        groupSearchPane.setVisible(true);
    }

    public void handleGroupNewButton(MouseEvent mouseEvent) {

        groupListPane.setVisible(false);
    }

    public void onGroupSearchCancelButtonPressed(ActionEvent actionEvent) {
        groupSearchPane.setVisible(false);
        groupListPane.setVisible(true);
    }

    public void onSearchGroupButtonClicked(ActionEvent actionEvent) {
        groupListPane.setVisible(false);
        groupSearchPane.setVisible(true);
    }

    public void onNewGroupClicked(ActionEvent actionEvent) {
        groupListPane.setVisible(false);
        listViewAddToGroup.setExpanded(true);
        listViewAddToGroup = contactListView;
        groupNewPane.setVisible(true);
    }

    public void onGroupNewCancelButtonPressed(ActionEvent actionEvent) {
        groupNewPane.setVisible(false);
        groupListPane.setVisible(true);
    }
}
