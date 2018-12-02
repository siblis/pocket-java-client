package client.controller;

import client.customFX.CFXListElement;
import client.model.ContactFullInfo;
import client.model.User;
import client.model.formatMsgWithServer.*;
import client.utils.Connector;
import client.utils.HTTPSRequest;
import client.view.ChatViewController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static client.utils.Common.showAlert;

public class ClientController implements Initializable {

    private static ClientController instance;
    private static String token;
    public WebEngine webEngine;
    private String msgArea;
    private HashMap<String, String> msgAreaMap = new HashMap<String, String>();
    private ObservableList<CFXListElement> contactsObservList;
    private ArrayList contactFIL;
    private String myNick;
    private String myId;
    private String sender;
    private String receiver = "24";
    private Connector conn = null;

    private ClientController() {
    }

    public static ClientController getInstance() {
        if (instance == null) {
            instance = new ClientController();
        }
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO
    }

    private void connect(String token) {
        conn = new Connector(token, ClientController.getInstance());
    }

    public String getMyNick() {
        return myNick;
    }

    public String getReceiver() {
        return receiver;
    }

    public WebEngine getWebEngine() {
        return webEngine;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    private void setSender(String sender) {
        this.sender = sender;
    }

    private boolean authentification(String login, String password) {
        if (!login.isEmpty() && !password.isEmpty()) {
            setSender(login);
            String answer = "0";
            AuthToServer ATS = new AuthToServer(login, password);
            String reqJSON = new Gson().toJson(ATS);
            try {
                answer = HTTPSRequest.authorization(reqJSON);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (answer.contains("token")) {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                AuthFromServer AFS = gson.fromJson(answer, AuthFromServer.class);
                System.out.println(" answer server " + AFS.getToken());
                token = AFS.getToken();
                connect(token);
                updateUserinfo(token);
                return true;
            } else {
                showAlert("Ошибка авторизации!", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Неполные данные для авторизации!", Alert.AlertType.ERROR);
            return false;
        }
        return false;
    }

    public void convertMFStoMessage(String jsonText) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        MessageFromServer MFS = gson.fromJson(jsonText, MessageFromServer.class);
        reciveMessage(MFS.getSender_name(), MFS.getMessage(), MFS.getReceiver(), MFS.getSenderid());
    }

    public void sendMessage(String sender, String receiver, String message) {
        setSender(sender);
        MessageToServer MTS = new MessageToServer(receiver, message);

        System.out.println(new Gson().toJson(MTS));
        conn.getChatClient().send(new Gson().toJson(MTS));

        reciveMessage(sender, message, receiver, myId);
    }

    private void reciveMessage(String senderName, String message, String receiverId, String senderId) {
        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = " [" + dateFormat.format(dateNow) + "]: ";

        String formatSender = "<b><font color = " + (myNick.equals(senderName) ? "green" : "red") + ">"
                + senderName
//                + " from " + senderId
//                + " to " + receiverId
                + "</font></b>"
                + time;


        String chatId = senderId.equals(myId) ? receiverId : senderId;
        msgArea = msgAreaMap.get(chatId) + formatSender + message + "<br>";
        msgAreaMap.put(chatId, msgArea);

        indicatorGetMessage(senderId);

        wievChat(receiver);
    }

    public void clientChoice(JFXListView<CFXListElement> contactList, MouseEvent event) {
        if (event.getClickCount() == 1) {
            int selected = contactList.getSelectionModel().getSelectedIndex();
            ContactFullInfo CFI = (ContactFullInfo) contactFIL.get(selected);
            CFI.setNoReadMessage(-1);
            receiver = (CFI.getUser().getUid());
            indicatorGetMessage(receiver);
            wievChat(receiver);
        }
    }

    public void disconnect() {
        if (conn != null)
            conn.getChatClient().close();
    }

    public void addContact(String contact) {
//        User user = new User(contact);
        AddContactToServer ACTS = new AddContactToServer(contact);
        String requestJSON = new Gson().toJson(ACTS);
        try {
            String answer = HTTPSRequest.addContact(requestJSON, token);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            if (answer.equals("400")) {
                showAlert("Ошибка добавления: " + contact + "  код 400", Alert.AlertType.ERROR);
            } else if (answer.equals("404")) {
                showAlert("Пользователь с email: " + contact + " не найден", Alert.AlertType.ERROR);
            } else if (answer.equals("409")) {
                showAlert("Пользователь с email: " + contact + " Уже в Вашем списке", Alert.AlertType.ERROR);
            } else {
                User user = gson.fromJson(answer, User.class);
                addToList(user);
                showAlert("Контакт " + user.getAccount_name() + " успешно добавлен", Alert.AlertType.INFORMATION);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addToList(User user) {
//     новый код
        if (!containsUserInCFIList(user)) {
            contactFIL.add(new ContactFullInfo(user));
        }
//
        contactsObservList = ChatViewController.getContactList();
        if (!contactsObservList.contains(user.getUid() + " " + user.getAccount_name())) {
            CFXListElement cfxListElement = new CFXListElement();
            try {
                //Если получать URL аватара пользователя, то можно подгружать его тут
                File file = new File("src/main/resources/client/images/ava.png");
                if (cfxListElement!=null) {
                    cfxListElement.setTopic(user.getAccount_name());
                    cfxListElement.setBody("Последнее сообщение");
                    cfxListElement.setAvatar((new Image(new FileInputStream(file))));
                //сдесь необходимо написать все инфо для инициализации

                }
            } catch (FileNotFoundException ex2){
                ex2.printStackTrace();
            }
            contactsObservList.add(cfxListElement);
        }
    }

    public void proceedRegister(String login, String password, String email) {
        RegToServer RTS = new RegToServer(login, email, password);
        String reqJSON = new Gson().toJson(RTS);

        try {
            int responseCode = HTTPSRequest.registration(reqJSON);
            if (responseCode == 201) {
                showAlert("Вы успешно зарегистрированы", Alert.AlertType.INFORMATION);
            } else
                showAlert("Ошибка регистрации, код: " + responseCode, Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean proceedLogIn(String login, String password) {
        return authentification(login, password);
    }

    public void updateContactList() {
        String jsonContacts = "{}";
        try {
            jsonContacts = HTTPSRequest.getContact(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(jsonContacts);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Type itemsMapType = new TypeToken<Map<String, GetUserListFromServer>>() {
        }.getType();
        Map<String, GetUserListFromServer> mapItemsDes = new Gson().fromJson(jsonContacts, itemsMapType);
        System.out.println(mapItemsDes.toString());

        for (Map.Entry<String, GetUserListFromServer> entry : mapItemsDes.entrySet()) {
            System.out.println(
                    entry.getValue().getId() + " " +
                            entry.getValue().getName() + " " +
                            entry.getKey());
            addToList(new User(entry.getValue().getId(), entry.getValue().getName(), entry.getKey()));
            msgAreaMap.put(entry.getValue().getId(), "");
        }
    }

    public void updateUserinfo(String token) {
        String userInfo = "{}";
        try {
            userInfo = HTTPSRequest.getSelfUser(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        User myAccount = jsonToUser(userInfo);
        myNick = myAccount.getAccount_name();
        myId = myAccount.getUid();
    }

    private void wievChat(String chatId) {
        webEngine.loadContent("<html>" +
                "<body>" +
                "<p>" +
                "<style>" +
                "div { font-size: 14px; white-space: pre-wrap;} html { overflow-x:  hidden; }" +
                "</style>" +
//                msgArea +
                msgAreaMap.get(chatId) +
                "<script>" +
                "javascript:scroll(0,10000)" +
                "</script>" +
                "</p>" +
                "</body>" +
                "</html>");
    }

    private void indicatorGetMessage(String senderId) {
        int index = indexIdfromCFIList(senderId);
        if (index == -1) {
            String getUser = "";
            try {
                getUser = HTTPSRequest.getUser(token, senderId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            addContact(jsonToUser(getUser).getEmail());
//            add user
        }
        ContactFullInfo CFI = (ContactFullInfo) contactFIL.get(index);
        if (senderId != myId) {
            CFI.incNoReadMessage();
        }
        CFXListElement lbl = contactsObservList.get(index);
        lbl.setUnreadMessages(""+CFI.getNoReadMessage());
        contactsObservList.set(index, lbl);
    }

    private boolean containsUserInCFIList(User user) {
        for (int i = 0; i < contactFIL.size(); i++) {
            if (((ContactFullInfo) contactFIL.get(i)).getUser().getUid().equals(user.getUid())) {
                return true;
            }
        }
        return false;
    }

    public void initContactFIL() {
        contactFIL = new ArrayList();
    }

    private int indexIdfromCFIList(String index) {
        for (int i = 0; i < contactFIL.size(); i++) {
            if (((ContactFullInfo) contactFIL.get(i)).getUser().getUid().equals(index)) {
                return i;
            }
        }
        return -1;
    }

    private User jsonToUser(String jsonuser) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonuser, User.class);
    }
}