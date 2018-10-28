package client.controller;

import client.model.ServerResponse;
import client.model.formatMsgWithServer.*;
import client.utils.Connector;
import client.utils.HTTPSRequest;
import client.view.ChatViewController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import database.dao.DataBaseService;
import database.entity.User;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static client.utils.Common.showAlert;

public class ClientController {

    private static ClientController instance;
    private static String token;
    private ChatViewController chatViewController;
    public WebEngine webEngine;
    private String msgArea = "";
    private String myNick;
    private String sender;
    private String receiver = "24";
    private Connector conn = null;
    private List<Long> contactList;

    private DataBaseService dbService;

    public void setChatViewController(ChatViewController chatViewController) {
        this.chatViewController = chatViewController;
    }

    private ClientController() {
    }

    public static ClientController getInstance() {
        if (instance == null) {
            instance = new ClientController();
        }
        return instance;
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

    private boolean authentication(String login, String password) {
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

                synchronizeContactList();

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

    public void receiveMessage(String message) {
        MessageFromServer mfs = convertMessageToMFS(message);
        if (!contactList.contains(mfs.senderid)) {
            try {
                ServerResponse response = HTTPSRequest.getUser(mfs.senderid, token);
                switch (response.getResponseCode()) {
                    case 200:
                        addContact(convertContactToCFS(response.getResponseJson()).getEmail());
                        break;
                    case 404:
                        showAlert("Пользователь с id: " + mfs.senderid + " не найден", Alert.AlertType.ERROR);
                        break;
                    default:
                        showAlert("Общая ошибка!", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showMessage(mfs.sender_name, mfs.message);
    }

    private MessageFromServer convertMessageToMFS(String jsonText) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonText, MessageFromServer.class);
    }

    private void showMessage(String senderName, String message) {
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

    public void sendMessage(String sender, String receiver, String message) {
        setSender(sender);
        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        String mess = " [" + dateFormat.format(dateNow) + "]: " + message;
        MessageToServer MTS = new MessageToServer(receiver, mess);

        System.out.println(new Gson().toJson(MTS));
        conn.getChatClient().send(new Gson().toJson(MTS));

        showMessage(sender, " [" + dateFormat.format(dateNow) + "]: " + message);
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

    private Map<String, ContactListFromServer> convertContactListToMap(String jsonText) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type itemsMapType = new TypeToken<Map<String, ContactListFromServer>>() {}.getType();
        return gson.fromJson(jsonText, itemsMapType);
    }

    private void synchronizeContactList() {
        dbService = new DataBaseService();
        contactList = dbService.getAllUserId();

        try {
            ServerResponse response = HTTPSRequest.getContacts(token);
            if (response != null) {
                Map<String, ContactListFromServer> map = convertContactListToMap(response.getResponseJson());
                for (Map.Entry<String, ContactListFromServer> entry : map.entrySet()) {
                    if (!contactList.contains(entry.getValue().getId())) {
                        ContactFromServer cfs = new ContactFromServer();
                        cfs.setUid(entry.getValue().getId());
                        cfs.setAccount_name(entry.getValue().getName());
                        cfs.setEmail(entry.getKey());

                        addContactToDB(cfs);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ContactFromServer convertContactToCFS(String jsonText) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonText, ContactFromServer.class);
    }

    public void addContact(String contact) {
        ContactToServer cts = new ContactToServer(contact);
        String requestJSON = new Gson().toJson(cts);

        try {
            ServerResponse response = HTTPSRequest.addContact(requestJSON, token);
            switch (response.getResponseCode()) {
                case 201:
                    showAlert("Контакт " + contact + " успешно добавлен", Alert.AlertType.INFORMATION);
                    addContactToDB(convertContactToCFS(response.getResponseJson()));
                    if (chatViewController != null) chatViewController.fillContactListView();
                    break;
                case 404:
                    showAlert("Пользователь с email: " + contact + " не найден", Alert.AlertType.ERROR);
                    break;
                case 409:
                    showAlert("Пользователь " + contact + " уже есть в списке ваших контактов", Alert.AlertType.ERROR);
                    break;
                default:
                    showAlert("Общая ошибка!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addContactToDB(ContactFromServer contact) {
        dbService.insertUser(new User(contact.getUid(), contact.getAccount_name(), contact.getEmail()));
        contactList.add(contact.getUid());
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
        return authentication(login, password);
    }


    public List<String> getAllUserNames() {
        return dbService.getAllUserNames();
    }

    public void dbServiceClose() {
        dbService.close();
    }
}