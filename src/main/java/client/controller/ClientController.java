package client.controller;

import client.model.User;
import client.model.formatMsgWithServer.*;
import client.utils.Connector;
import client.utils.HTTPSRequest;
import client.view.ChatViewController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;

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
    private ObservableList<String> contactsObservList;
    private String myNick;
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
                myNick = login;
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
        reciveMessage(MFS.getSender_name(), MFS.getMessage());
    }

    public void sendMessage(String sender, String receiver, String message) {
        setSender(sender);
        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        String mess = " [" + dateFormat.format(dateNow) + "]: " + message;
        MessageToServer MTS = new MessageToServer(receiver, mess);

        System.out.println(new Gson().toJson(MTS));
        conn.getChatClient().send(new Gson().toJson(MTS));

        reciveMessage(sender, " [" + dateFormat.format(dateNow) + "]: " + message);
    }

    private void reciveMessage(String senderName, String message) {
        String formatSender = "<b><font color = " + (myNick.equals(senderName) ? "green" : "red") + ">"
                + senderName
                + "</font></b>";

        msgArea += formatSender + message + "<br>";
        webEngine.loadContent("<html>" +
                "<body>" +
                "<p>" +
                "<style>" +
                "div { font-size: 16px; white-space: pre-wrap;} html { overflow-x:  hidden; }" +
                "</style>" +
                msgArea +
                "<script>" +
                "javascript:scroll(0,10000)" +
                "</script>" +
                "</p>" +
                "</body>" +
                "</html>");
    }

    public void clientChoice(ListView<String> contactList, MouseEvent event) {
        if (event.getClickCount() == 1) {
            receiver = contactList.getSelectionModel().getSelectedItem().split(" ")[0];
            showAlert("Сообщения будут отправляться контакту " + receiver, Alert.AlertType.INFORMATION);
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

            if (answer.equals("404")) {
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
        contactsObservList = ChatViewController.getContactList();
        if (!contactsObservList.contains(user.getUid() + " " + user.getAccount_name())) {
            contactsObservList.add(user.getUid() + " " + user.getAccount_name());
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

        Type itemsMapType = new TypeToken<Map<String, GetUserListFromServer>>() {}.getType();
        Map<String, GetUserListFromServer> mapItemsDes = new Gson().fromJson(jsonContacts, itemsMapType);
        System.out.println(mapItemsDes.toString());

        for (GetUserListFromServer GULFS : mapItemsDes.values()
        ) {
            System.out.println(GULFS.getId()+" "+ GULFS.getName());
            addToList(new User(GULFS.getId(),GULFS.getName()));
        }
    }
}