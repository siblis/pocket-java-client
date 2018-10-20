package client.controller;

import client.model.User;
import client.model.formatMsgWithServer.AuthFromServer;
import client.model.formatMsgWithServer.AuthToServer;
import client.model.formatMsgWithServer.MessageFromServer;
import client.model.formatMsgWithServer.MessageToServer;
import client.utils.Connector;
import client.utils.HTTPSRequest;
import client.view.ChatViewController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

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
                System.out.println(" answer server " + AFS.token);
                token = AFS.token;
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
        reciveMessage(MFS.sender_name, MFS.message);
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
                +"</font></b>";

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
            receiver = contactList.getSelectionModel().getSelectedItem();
            showAlert("Сообщения будут отправляться контакту " + receiver, Alert.AlertType.INFORMATION);
        }
    }

    public void disconnect() {
        if (conn != null)
            conn.getChatClient().close();
    }

    public void addContact(String contact) {
        User user = new User(contact);
        String requestJSON = new Gson().toJson(user);
        try {
            int answer = HTTPSRequest.addContact(requestJSON, token);
            if (answer == 201) {
                addToList(user.getContact());
            } else {
                showAlert("Пользователь с email: " + contact + " не найден", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addToList(String uid) {
//         в дальнейшем будет добавлен User , а не id юзера
        contactsObservList = ChatViewController.getContactList();
        if (!contactsObservList.contains(uid)) {
            contactsObservList.add(uid);
            showAlert("Контакт " + uid + " успешно добавлен", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Пользователь " + uid + " уже есть в списке ваших контактов", Alert.AlertType.ERROR);
        }
    }

    public void proceedRegister(String login, String password, String email) {
        String requestJSON = "{" +
                "\"account_name\": \"" + login + "\"," +
                "\"email\": \"" + email + "\"," +
                "\"password\": \"" + password + "\"" +
                "}";
        try {
            int responseCode = HTTPSRequest.registration(requestJSON);
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


}