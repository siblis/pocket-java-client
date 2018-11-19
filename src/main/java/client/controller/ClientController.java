package client.controller;

import client.model.ServerResponse;
import client.model.formatMsgWithServer.*;
import client.utils.Common;
import client.utils.Connector;
import client.utils.HTTPSRequest;
import client.utils.Sound;
import client.view.ChatViewController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import database.dao.DataBaseService;
import database.entity.Message;
import database.entity.User;
import javafx.scene.control.*;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static client.utils.Common.showAlert;

public class ClientController {

    private static ClientController instance;
    private static String token;
    private ChatViewController chatViewController;

    private String msgArea = "";
    private User receiver = null;
    private User sender = null;
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

    public String getSenderName() {
        return sender.getAccount_name();
    }

    public void setReceiver(long receiverId) {
        this.receiver = dbService.getUser(receiverId);
        loadChat();
    }

    public void setReceiver(String receiver) {
        this.receiver = dbService.getUserByName(receiver);
        loadChat();
    }

    private boolean authentication(String login, String password) {
        if (!login.isEmpty() && !password.isEmpty()) {
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

                try {
                    ServerResponse response = HTTPSRequest.getMySelf(token);
                    sender = convertJSONToUser(response.getResponseJson());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sender.setAccount_name(login);
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

    private MessageFromServer convertMessageToMFS(String jsonText) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonText, MessageFromServer.class);
    }

    public void receiveMessage(String message) {
        MessageFromServer mfs = convertMessageToMFS(message);
        if (!contactList.contains(mfs.getSenderid())) {
            try {
                ServerResponse response = HTTPSRequest.getUser(mfs.getSenderid(), token);
                switch (response.getResponseCode()) {
                    case 200:
                        addContact(convertJSONToUser(response.getResponseJson()).getEmail());
                        break;
                    case 404:
                        showAlert("Пользователь не найден", Alert.AlertType.ERROR);//с id: " + mfs.getSenderid() + "
                        break;
                    default:
                        showAlert("Общая ошибка!", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showMessage(mfs.getSender_name(), mfs.getMessage(), mfs.getTimestamp(),true);

        dbService.addMessage(mfs.getReceiver(),
                mfs.getSenderid(),
                new Message(mfs.getMessage(),
                        mfs.getTimestamp()));
    }

    private void showMessage(String senderName, String message, Timestamp timestamp,boolean isNew) {
        if (isNew){
            Sound.playSound("src\\main\\resources\\client\\sounds\\1.wav").join();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        String formatSender = "<b><font color = " + (sender.getAccount_name().equals(senderName) ? "green" : "red") + ">"
                + senderName
                + "</font></b>";

        message = message.replaceAll("\n", "<br/>");
        message = Common.urlToHyperlink(message);

        msgArea += dateFormat.format(timestamp) + " " + formatSender + " " + message + "<br>";

        chatViewController.webEngine.loadContent("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "</head>\n" +

                "<body style=\"background-image: url(" + chatViewController.getChatBackgroundImage().toURI().toString() + ")\">\n" +

                "        <div id=\"messageArea\">" +
                msgArea +
                "       </div>\n" +

                "    </body>\n" +
                "</html>");
    }

    public void sendMessage(String message) {
        if (receiver == null){
            showAlert("Выберите контакт для отправки сообщения", Alert.AlertType.ERROR);
            return;
        }
        MessageToServer MTS = new MessageToServer(receiver.getUid(), message);

        String jsonMessage = new Gson().toJson(MTS);
        System.out.println(jsonMessage);
        conn.getChatClient().send(jsonMessage);

        dbService.addMessage(receiver.getUid(),
                sender.getUid(),
                new Message(message, new Timestamp(System.currentTimeMillis()))
        );
        showMessage(sender.getAccount_name(), message, new Timestamp(System.currentTimeMillis()),false);
    }

    private void loadChat() {
        List<Message> converstation = dbService.getChat(sender, receiver);
        msgArea = "";
        showMessage("", "", new Timestamp(0),false);// не очень удачная (плохая) попытка очистить WebView
        for (Message message :
                converstation) {
            showMessage(message.getSender().getAccount_name(), message.getText(), message.getTime(),false);
        }
    }

    public void disconnect() {
        if (conn != null)
            conn.getChatClient().close();
    }

    private Map<String, ContactListFromServer> convertContactListToMap(String jsonText) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type itemsMapType = new TypeToken<Map<String, ContactListFromServer>>() {
        }.getType();
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
                        User user = new User();
                        user.setUid(entry.getValue().getId());
                        user.setAccount_name(entry.getValue().getName());
                        user.setEmail(entry.getKey());

                        addContactToDB(user);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // проверяем, есть ли наш пользователь в БД
        User user = dbService.getUser(sender.getUid());
        if (user == null){
            dbService.insertUser(sender);
        }
    }

    private User convertJSONToUser(String jsonText) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonText, User.class);
    }

    public void addContact(String contact) {
        UserToServer cts = new UserToServer(contact);
        String requestJSON = new Gson().toJson(cts);

        try {
            ServerResponse response = HTTPSRequest.addContact(requestJSON, token);
            switch (response.getResponseCode()) {
                case 201:
                    showAlert("Контакт " + contact + " успешно добавлен", Alert.AlertType.INFORMATION);
                    addContactToDB(convertJSONToUser(response.getResponseJson()));
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

    private void addContactToDB(User contact) {
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

    public String proceedRestorePassword(String email) {
        String answer = "0";
        String requestJSON = "{" +
                "\"email\": \"" + email + "\"" +
                "}";
        try {
            answer = HTTPSRequest.restorePassword(requestJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return answer;
    }

    public String proceedChangePassword(String email, String codeRecovery, String password) {
        String answer = "0";
        String requestJSON = "{" +
                "\"email\": \"" + email + "\"," +
                "\"code\": \"" + codeRecovery + "\"," +
                "\"password\": \"" + password + "\"" +
                "}";
        try {
            answer = HTTPSRequest.changePassword(requestJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return answer;
    }
}