package ru.geekbrains.pocket.messenger.client.view;

import ru.geekbrains.pocket.messenger.client.Main;
import ru.geekbrains.pocket.messenger.client.controller.ClientController;
import ru.geekbrains.pocket.messenger.client.utils.Common;
import ru.geekbrains.pocket.messenger.client.utils.CustomTextArea;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import ru.geekbrains.pocket.messenger.client.view.customFX.*;
import ru.geekbrains.pocket.messenger.database.entity.Message;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ChatViewController implements Initializable {

    private static ChatViewController instance;

    @FXML
    private BorderPane borderPaneMain;
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
    private ImageView contactsImage;

    @FXML
    private ImageView chatsImage;

    @FXML
    private AnchorPane userSearchPane;

    @FXML
    private AnchorPane contactsViewPane;

    @FXML
    private AnchorPane groupSearchPane;

    @FXML
    private AnchorPane groupListPane;

    @FXML
    private AnchorPane groupNewPane;

    @FXML
    private AnchorPane contactSearchPane;

    @FXML
    private ScrollPane myProfileScrollPane;

    @FXML
    private ScrollPane groupProfileScrollPane;

    @FXML
    private ScrollPane otherProfileScrollPane;

    @FXML
    private JFXListView<?> groupListView;

    @FXML
    private JFXListView<?> groupSearchListView;

    @FXML
    private JFXListView<CFXListElement> listViewAddToGroup;

    @FXML
    private Menu menuLeft;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXTextField creategroupName;

    @FXML
    private CFXMyProfile myProfile;

    @FXML
    private CFXGroupProfile groupProfile;

    @FXML
    private CFXOtherProfile othersProfile;

    @FXML
    private JFXTextField tfSearchInput;

    @FXML
    private JFXTextField userSearchText;

    @FXML
    private JFXTabPane tabPane;
    //
    private WebEngine webEngine;

    private ObservableList<CFXListElement> contactsObservList;

    private ClientController clientController;

    private String backgroundImage;

    private Document DOMdocument;

    private String tsOld;

    private int idDivMsg;

    private int idMsg;

    @FXML
    private  JFXButton btnContactSearchCancel;

    @FXML
    private JFXButton btnContactSearchInvite;

    @FXML
    private JFXListView<CFXListElement> searchListView;
    private ObservableList<CFXListElement> searchObsList;

    @FXML
    private CFXMenuLeft cfxMenuLeft;

    @FXML
    private CFXMenuRightGroup cfxMenuRightGroup;

    @FXML
    private JFXButton btnRightMenu;

    public static ChatViewController getInstance() {
        return instance;
    }

    private SingleSelectionModel<Tab> selectionModel;
    //ссылка на desktop
    private Desktop desktop;
    ////////////////////////
    HamburgerBasicCloseTransition transition;
    HamburgerBackArrowBasicTransition transitionBack;

    public ChatViewController() {
    }

    public int getIdMsg() {
        return idMsg;
    }

    public void setIdMsg(int idMsg) {
        this.idMsg = idMsg;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DOMdocument = null;
        tsOld = null; //чистка даты
        idMsg = 0; //присваивание ID

        webEngine = messageWebView.getEngine(); //инициализация WebEngine
        initBackgroundWebView();
        initWebView();

        clientController = ClientController.getInstance();
        clientController.setChatViewController(this);
        contactsObservList = FXCollections.observableList(clientController.getContactListOfCards());
        // при пустом списке контактов открыть вкладку контакты //todo перепилить на список чатов?
        if (contactsObservList.isEmpty()) 
            contacts.getTabPane().getSelectionModel().select(contacts);

        contactListView.setExpanded(true);
        fillContactListView();
        searchObsList = FXCollections.observableList(new ArrayList<CFXListElement>());
        searchListView.setExpanded(true);
        searchListView.setItems(searchObsList);

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
        transition = new HamburgerBasicCloseTransition(hamburger);
        transitionBack = new HamburgerBackArrowBasicTransition(hamburger);
        PaneProvider.setTransitionBack(transitionBack);
         selectionModel=tabPane.getSelectionModel();
         instance=this;
         CFXMenuLeft.setParentController(instance);
         PaneProvider.setBorderPaneMain(borderPaneMain);

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
                            "width: 33px; \n"+
                            "height: 33px; \n"+
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

    private void fillContactListView() {
        contactListView.setItems(contactsObservList);
        //todo: загрузка последних сообщений в body элементов
    }

    public void updateContactListView() {
        if (!contactsViewPane.isVisible() && contactSearchPane.isVisible()) contactSearchBtnCancelClicked();
        contactListView.setItems(null);
        contactListView.setItems(contactsObservList);
        contactListView.refresh();
    }

    //  инициализация картинки аватара
    //if sex = true, is a woman
    //   sex = false, is a man
    private String initAvatar(boolean sex) {
        String path = "";
        if (sex) {
            path = "client/images/defaultAvatar/girl.png"; //картинка фона
        }else {
            path = "client/images/defaultAvatar/man.png"; //картинка фона
        }
        ClassLoader cl = this.getClass().getClassLoader();
        String avatar = "";
        try {
            avatar = cl.getResource(path).toURI().toString();
        }catch (Exception e) {
            //todo перенести в логирование
            e.printStackTrace();
        }
        return avatar;
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
    private void createMessageDiv(Message mess, String attrClass){

        String message = mess.getText();
        String senderName = mess.getSender().getUserName();
        Timestamp timestamp = mess.getTime();

        //ID требуется для скрипта вставки тегов
        idMsg+=1;
        setIdMsg(idMsg);
        //получаем аватар
        //тут по идеи подбор по полу. Оставляю чтобы было понятно куда вставляется и настроить стили
        String avatar = initAvatar(false); //man
        String styleStr = "background-image: url(" + avatar + "); background-size: cover";
        //

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
        divLogo.setAttribute("style", styleStr);
        divTxt.setAttribute("class", attrClass+" msgTxt");
        divTxtSender.setAttribute("class", attrClass+"S sender");
        divTxtMsg.setAttribute("class", attrClass+"M msg");
        divTxtMsg.setAttribute("id", String.valueOf(idMsg)); //id
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
        //проверяет, есть ли у нас в сообщениях картинки
        addImageMessageListener(divTxtMsg);
    }

    public void showMessage(Message mess, boolean isNew) {
        /*if (isNew) {
            Sound.playSoundNewMessage().join();
        }*/

        String senderName = mess.getSender().getUserName();

        String attrClass;
        if (clientController.getSenderName().equals(senderName)) {
            attrClass = "myUserClass";
        } else {
            attrClass = "senderUserClass";
        }

        //todo по хорошему надо переместить подписку на событие в другое место
        //Подписка на событие загрузки документа HTML in WebView
        if (DOMdocument == null) {
            //если пользователь только запустил клиента и локально нет ни одного сообщения
            if (webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
                DOMdocument = webEngine.getDocument();
                createMessageDiv(mess, attrClass);
                updateLastMessageInCardsBody(mess);
            }else {
                webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        DOMdocument = webEngine.getDocument(); // Должен быть здесь т.к. загрузка WebEngine только произошла
                        createMessageDiv(mess, attrClass);
                        updateLastMessageInCardsBody(mess);
                    }
                });
            }
        }else {
            createMessageDiv(mess, attrClass);
            updateLastMessageInCardsBody(mess);
        }
    }

    private void updateLastMessageInCardsBody(Message mess){
        CFXListElement targetChat = null;

        String message = mess.getText();
        String senderName = mess.getSender().getUserName();
        String recieverName = mess.getReceiver().getUserName();
        Timestamp timestamp = mess.getTime();

        String myUser = clientController.getMyUser().getUserName();

        for (CFXListElement element : contactsObservList){
            if ((element.getUser().getUserName().equals(senderName)
                    & myUser.equals(recieverName))
                    | (element.getUser().getUserName().equals(recieverName)
                    & myUser.equals(senderName))) {
                targetChat = element;
                break;
            }
        }
        if (targetChat == null) return; //TODO определить вероятность и доделать (вывод ошибки пользователю, лог)
        targetChat.setBody(senderName + ": " + message);
        SimpleDateFormat dateFormatDay = initDateFormat("dd.MM.YYYY");
        targetChat.setDateText(dateFormatDay.format(timestamp));
    }

    @FXML
    public void handleDisconnectButton() {
        Stage stage = (Stage) messagePanel.getScene().getWindow();
        stage.close();
        clientController.disconnect();
        Tray.currentStage = null;
        Main.initRootLayout();
        Main.showOverview();
    }

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
        String receiver = contactListView.getSelectionModel().getSelectedItem().getUser().getId();
        if (event.getClickCount() == 1) {
            //showAlert("Сообщения будут отправляться контакту " + receiver, Alert.AlertType.INFORMATION);
            clientController.setReceiver(receiver);
            messageField.requestFocus();
            messageField.selectEnd();
        } else if (event.getClickCount() == 2) {
            othersProfile.setUser(
                    contactListView.getSelectionModel().getSelectedItem().getUser());
            othersProfile.setIfFriendly(true);
            PaneProvider.setProfileScrollPane(otherProfileScrollPane);
            paneProvidersProfScrollPaneVisChange(true);
        }
    }

    @FXML
    private void handleFindedClientChoice(MouseEvent event) {
        String receiver = searchListView.getSelectionModel().getSelectedItem().getUser().getId();
        if (event.getClickCount() == 1) {
            if (clientController.hasReceiver(receiver)) {
                btnContactSearchInvite.setVisible(false);
                clientController.setReceiver(receiver);
                messageField.requestFocus();
                messageField.selectEnd();
            } else {
                clearMessageWebView();
                btnContactSearchInvite.setVisible(true);
            }
        } else if (event.getClickCount() == 2) {
            othersProfile.setUser(
                    searchListView.getSelectionModel().getSelectedItem().getUser());
            othersProfile.setIfFriendly(clientController.hasReceiver(receiver));
            PaneProvider.setProfileScrollPane(otherProfileScrollPane);
            paneProvidersProfScrollPaneVisChange(true);
        }
    }

    private void paneProvidersProfScrollPaneVisChange(boolean newVisStat) {
        PaneProvider.getProfileScrollPane().setVisible(newVisStat);
        if (newVisStat) PaneProvider.getProfileScrollPane().setVvalue(0f); //scroll to top
    }

    //обработка воспроизведения картинок
    private void addImageMessageListener(Element tagElement) {
        NodeList nodeList = tagElement.getElementsByTagName("img");
        for (int i = 0; i < nodeList.getLength(); i++) {
            initSmile();
        }
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

    //метод для инициализации картинок. Временны пока нет загрузки картинок из бд
    public void initSmile() {
        String path = "client/smiley/wink.png";//пока имеем возможность загружать только один вид смайлика

        ClassLoader cl = this.getClass().getClassLoader();
        String emoji = "";
        try {
            emoji = cl.getResource(path).toURI().toString();
            webEngine.executeScript("document.getElementById(\"" + (getIdMsg()) + "\").innerHTML = '" + "<img src = \"" + emoji + "\" width=\"30\" alt=\"lorem\"/>" +"'");
        }catch (Exception e) {
            //todo перенести в логирование
            e.printStackTrace();
        }

    }

    //метод добавления смайликов
    @FXML
    public void handleSendSmile() {
        String img = "";
        File f = new File(getClass().getResource("/client/smiley").getFile());

        for (File fs : f.listFiles()) {
            img += fs.toURI();
            clientController.sendMessage(img);
            webEngine.executeScript("document.getElementById(\"" + idMsg + "\").innerHTML = '" + "<img src = \"" + img + "\" width=\"30\" alt=\"lorem\"/>" +"'");
            setIdMsg(idMsg++);
            webEngine.executeScript("document.getElementById(\"" + (getIdMsg()) + "\").innerHTML = '" + "<img src = \"" + img + "\" width=\"30\" alt=\"lorem\"/>" +"'");
        }
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
    @FXML
    public void handleOnChatSelected() {
        chatsImage.setImage(new Image("/client/images/chat/chatsActive.png"));
        if (contacts != null) {
            contactsImage.setImage(new Image("/client/images/chat/contacts.png"));
            contacts.setStyle("-fx-border-width: 0 0 5 0; " +
                    "-fx-border-color: #3498DB #3498DB transparent #3498DB;" +
                    "-fx-border-insets: 0;" +
                    "-fx-border-style: solid;" +
                    "-tab-text-color: #FFFFFF;");
        }
        chats.setStyle("-fx-border-width: 0 0 5 0; " +
                "-fx-border-color: transparent transparent #F8D57D transparent;" +
                "-fx-border-insets: 0;" +
                "-fx-border-style: solid;" +
                "-tab-text-color: #F8D57D;");
    }

    @FXML
    public void handleOnContactSelected() {
        contactsImage.setImage(new Image("/client/images/chat/contactsActive.png"));
        chatsImage.setImage(new Image("/client/images/chat/chats.png"));
        contacts.setStyle("-fx-border-width: 0 0 5 0; " +
                "-fx-border-color: transparent transparent #F8D57D transparent;" +
                "-fx-border-insets: 0;" +
                "-fx-border-style: solid;" +
                "-tab-text-color: #F8D57D;");
        chats.setStyle("-fx-border-width: 0 0 5 0; " +
                "-fx-border-color: #3498DB #3498DB transparent #3498DB;" +
                "-fx-border-insets: 0;" +
                "-fx-border-style: solid;" +
                "-tab-text-color: #FFFFFF;");
    }

    private ImageView buildImage(String s) {
        Image i = new Image(s);
        ImageView imageView = new ImageView();
        imageView.setImage(i);
        return imageView;
    }

    @FXML
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

    @FXML
    public void onNewGroupClicked(ActionEvent actionEvent) {
        selectionModel.select(0);
        cfxMenuLeft.setVisible(false);
        menuLeft.hide();
        groupListPane.setVisible(false);
        listViewAddToGroup.setExpanded(true);
        groupNewPane.setVisible(true);
    }

    @FXML
    public void onGroupNewCancelButtonPressed(ActionEvent actionEvent) {
        groupNewPane.setVisible(false);
        groupListPane.setVisible(true);
    }

    @FXML
    public void onMyProfileOpen(ActionEvent actionEvent) {
        PaneProvider.setProfileScrollPane(myProfileScrollPane);
        cfxMenuLeft.setVisible(false);
        menuLeft.hide();
        myProfile.setUser(clientController.getMyUser());
        paneProvidersProfScrollPaneVisChange(true);

        PaneProvider.getTransitionBack().setRate(1);
        PaneProvider.getTransitionBack().play();
    }

    @FXML
    public void onHamburgerClicked(MouseEvent mouseEvent) {
        if (myProfileScrollPane.isVisible()) {
            myProfileScrollPane.setVisible(false);
            PaneProvider.getTransitionBack().setRate(-1);
            transitionBack.play();
        }
        else if (!menuLeft.isShowing()){
            transition.setRate(1);
            transition.play();
//            menuLeft.show();
            cfxMenuLeft.setVisible(true);
        } else {
            menuLeft.hide();
            cfxMenuLeft.setVisible(false);
        }

    }

    @FXML
    public void onHideMenuLeft(javafx.event.Event event) {
        transition.setRate(-1);

        transition.play();
    }

    public void handleGroupJoinButton(){
//        clientController.joinGroup(groupName.getText());
    }

    @FXML
    public void handleGroupCreateButton(){
        clientController.addGroup(creategroupName.getText());
    }

    @FXML
    public void findContact(KeyEvent keyEvent) {
        if (tfSearchInput.getText().length()>0) {
            searchObsList.clear();
            contactsViewPane.setVisible(false);
            contactSearchPane.setVisible(true);
            contactsObservList.forEach(elem -> {
                if ( //elem.getUser().getEmail().contains(tfSearchInput.getText()) ||
                        elem.getUser().getUserName().contains(tfSearchInput.getText())) {
                    CFXListElement temp = new CFXListElement();
                    temp.setUser(elem.getUser());
                    //temp.setBody(elem.getUser().getEmail());
                    searchObsList.add(temp);
                }
            });
            // todo: поиск на сервере от 2х символов, убрать/расширить ограничение?
            if (tfSearchInput.getText().length()>=6) {
                CFXListElement searchFromServer = clientController.findContact(tfSearchInput.getText());
                //todo: статус пользователей (онлайн/офлайн) - будет приходить с сервера или запрашивать на каждого?
//                if (searchFromServer != null) {
//                    searchFromServer.removeAll(searchObsList);
//                    searchFromServer.remove(new CFXListElement(clientController.getMyUser()));
//                    searchFromServer.forEach(elem -> {
//                        CFXListElement temp = new CFXListElement();
//                        temp.setUser(elem.getUser());
//                        temp.setBody(elem.getUser().getEmail());
//                        searchObsList.add(temp);
//                    });
//                }
                if (searchFromServer != null) searchObsList.add(searchFromServer);
            }
            selectionModel.select(1);
            searchListView.refresh();
            if (btnContactSearchInvite.isVisible()) btnContactSearchInvite.setVisible(false);
        } else {
            contactsViewPane.setVisible(true);
            contactSearchPane.setVisible(false);
        }

    }

    @FXML
    private void contactSearchBtnInviteClicked() {
        clientController.addContact(searchListView.getSelectionModel().getSelectedItem().getUser());
        contactSearchBtnCancelClicked();
    }

    @FXML
    private void contactSearchBtnCancelClicked() {
        contactsViewPane.setVisible(true);
        tfSearchInput.setText("");
        contactSearchPane.setVisible(false);
    }

    @FXML
    public void onMouseExitMenu(MouseEvent mouseEvent) {
        cfxMenuLeft.setVisible(false);
        transition.setRate(-1);

        transition.play();
    }

    @FXML
    public void onMouseExitMenuRight(MouseEvent mouseEvent) {
        cfxMenuRightGroup.setVisible(false);
    }

    @FXML
    public void btnRightMenuClicked(ActionEvent actionEvent) {
        if (cfxMenuRightGroup.isVisible()){
            cfxMenuRightGroup.setVisible(false);
        } else {

            cfxMenuRightGroup.setVisible(true);
        }
    }
    public void alarmGroupQuitGroupExecute(){
        new AlarmGroupQuitGroup();
    }

    public void alarmGroupDeleteGroupExecute(){
        new AlarmDeleteGroup();
    }
    public void alarmDeleteMessageHistoryExecute(){
        new AlarmDeleteMessageHistory(null);
    }
    public void alarmDeleteProfileExecute(){
        new AlarmDeleteProfile(ProfileType.MY, null);
    }
    public void alarmExitProfileExecute(){
        new AlarmExitProfile();
    }
}
