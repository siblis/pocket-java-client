package ru.geekbrains.pocket.messenger.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.geekbrains.pocket.messenger.client.model.Group;
import ru.geekbrains.pocket.messenger.client.model.ServerResponse;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.*;
import ru.geekbrains.pocket.messenger.client.model.fromServer.RegistrationFromServer;
import ru.geekbrains.pocket.messenger.client.model.pub.UserPub;
import ru.geekbrains.pocket.messenger.client.model.toServer.RegistrationToServer;
import ru.geekbrains.pocket.messenger.client.utils.Connector;
import ru.geekbrains.pocket.messenger.client.utils.HTTPSRequest;
import ru.geekbrains.pocket.messenger.client.utils.Sound;
import ru.geekbrains.pocket.messenger.client.view.ChatViewController;
import ru.geekbrains.pocket.messenger.client.view.customFX.CFXListElement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ru.geekbrains.pocket.messenger.database.dao.DataBaseService;
import ru.geekbrains.pocket.messenger.database.entity.Message;
import ru.geekbrains.pocket.messenger.database.entity.User;
import ru.geekbrains.pocket.messenger.database.entity.UserProfile;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static ru.geekbrains.pocket.messenger.client.utils.Common.showAlert;

public class ClientController {
    private static final Logger controllerLogger = LogManager.getLogger(ClientController.class);
    private static ClientController instance;
    private static String token;
    private ChatViewController chatViewController;

    private User receiver;
    private User myUser;
    private Connector conn;
    private List<Long> contactList;
    private List<CFXListElement> contactListOfCards;

    private DataBaseService dbService;

    public void setChatViewController(ChatViewController chatViewController) {
        this.chatViewController = chatViewController;
    }

    private ClientController() {
        receiver = null;
        myUser = null;
        conn = null;
    }

    public User getMyUser() {
        return myUser;
    }

    public User getReciever() {
        return receiver;
    }

    public static ClientController getInstance() {
        if (instance == null) {
            instance = new ClientController();
        }
        return instance;
    }

    private void connect(String token) {
        conn = new Connector(token, getInstance());
    }

    public String getSenderName() {
        return myUser.getAccount_name();
    }

    public String getRecieverName() {
        return receiver.getAccount_name();
    }

    public void setReceiver(long receiverId) {
        this.receiver = dbService.getUser(receiverId);
        loadChat();
    }

    public void setReceiver(String receiverName) {
        this.receiver = dbService.getUser(receiverName);
        loadChat();
    }

    public void setReceiver(User receiver) {
        if (!contactList.contains(receiver.getId()))
            addContactToDbAndChat(receiver); // todo поправить логику получения сообщений?
            //NB: todo добавлять в список на сервере? addContactToDbAndChat не добавляет
        this.receiver = receiver;
        loadChat();
    }

    public boolean hasReceiver(long receiverId) {
        return dbService.getUser(receiverId) != null;
    }

    public boolean hasReceiver(String receiverName) {
        return dbService.getUser(receiverName) != null;
    }

    public List<CFXListElement> getContactListOfCards() {
        return contactListOfCards;
    }

    private boolean authentication(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()) {
            String answer = "0";
            try {
                answer = HTTPSRequest.authorization(new AuthToServer(email, password).toJson());
            } catch (Exception e) {
                controllerLogger.error("HTTPSRequest.authorization_error", e);
            }
            if (answer.contains("token")) {
                AuthFromServer auth = AuthFromServer.fromJson(answer);
                System.out.println(" answer server " + auth.token + "\n" + auth.user);
                token = auth.token;
                connect(token);
                myUser = auth.user;

                synchronizeContactList();

                return true;
            } else {
//                showAlert("Ошибка авторизации!", Alert.AlertType.ERROR);
                controllerLogger.info("Ошибка авторизации!", Alert.AlertType.ERROR);
            }
        } else {
//            showAlert("Неполные данные для авторизации!", Alert.AlertType.ERROR);
            controllerLogger.info("Неполные данные для авторизации!", Alert.AlertType.ERROR);
            return false;
        }
        return false;
    }

    public void receiveMessage(String message) {
        MessageFromServer mfs = MessageFromServer.fromJson(message);
        if (!contactList.contains(mfs.getSenderId())) { //Проверяем, что осообщение пришло не от клиента в списке
            try {
                ServerResponse response = HTTPSRequest.getUser(mfs.getSenderId(), null, token);
                switch (response.getResponseCode()) {
                    case 200:
                        addContact(convertUserProfileJSONToUser(response.getResponseJson()));
                        break;
                    case 404:
                        showAlert("Пользователь не найден", Alert.AlertType.ERROR);//с id: " + mfs.getSenderid() + "
                        break;
                    default:
                        showAlert("Общая ошибка!", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                controllerLogger.error("HTTPSRequest.getUser_error", e);
            }
        }
        //Проверяем что у нас чат именно с этим пользователем, иначе сообщение не выводится
        //Как будет с группами пока не понятно
        if (receiver.getId().equals(mfs.getSenderId())) {
            //todo на данный момент showMessage принимает senderName, а не senderId => допилить
            chatViewController.showMessage(mfs.getSenderId(), mfs.getMessage(), mfs.getTimestamp(), true);
        }
        if (mfs.getSenderId() != null) { //отключаем звук для служебных сообщений
            Sound.playSoundNewMessage().join(); //Звук нового сообщения должен быть в любом случае
        }
        dbService.addMessage(new Long(mfs.getReceiverId()),
                new Long(mfs.getSenderId()),
                new Message(mfs.getMessage(),
                        mfs.getTimestamp()));
    }

    public void sendMessage(String message) {
        if (receiver == null) {
            showAlert("Выберите контакт для отправки сообщения", Alert.AlertType.ERROR);
            return;
        }
        
        String jsonMessage = 
                new MessageToServer(message, null, receiver.getId().toString(), null).toJson();
        try {
            conn.getChatClient().send(jsonMessage);
            //todo допилить получение Success/Error и MessageId из ответа

//            dbService.addMessage(receiver.getId(),
//                    user.getId(),
//                    new Message(message, new Timestamp(System.currentTimeMillis()))
//            );
            chatViewController.showMessage(myUser.getAccount_name(), message, new Timestamp(System.currentTimeMillis()), false);

        } catch (IOException ex) {
            showAlert("Потеряно соединение с сервером", Alert.AlertType.ERROR);
            controllerLogger.error(ex);
        }

    }

    private void loadChat() {
        List<Message> converstation = dbService.getChat(myUser, receiver);
        chatViewController.clearMessageWebView();

        for (Message message :
                converstation) {
            chatViewController.showMessage(message.getSender().getAccount_name(), message.getText(), message.getTime(), false);
        }
    }

    public void disconnect() {
        if (conn != null) {
            conn.disconnect();
            conn = null;
        }
        if (dbService != null) {
            dbService.close();
            dbService = null;
        }
        instance = null;
        contactList = null;
        contactListOfCards = null;
    }

    private void synchronizeContactList() {
        dbService = new DataBaseService(myUser);
        contactList = dbService.getAllUserId();
        contactListOfCards = new ArrayList<>();
        
//        dbService.getAllUsers().forEach(user -> {
//            if (user.getId() != user.getId()) {
//                contactListOfCards.add(new CFXListElement(user));
//            }
//        });

        try {
            List<User> contactsToRemoveFromDb = dbService.getAllUsers();
            contactsToRemoveFromDb.remove(myUser);
            ServerResponse response;
            int pageOfContacts = 0;
            while (true) {
                response = HTTPSRequest.getContacts(token, pageOfContacts);
                ContactListFromServer clfs = ContactListFromServer.fromJson(response.getResponseJson());
                if (clfs.getContacts().length == 0) break;
                synchronizePageOfContListFromServ(clfs.getContacts(), contactsToRemoveFromDb);
            }

            //удаляем из локальной базы контакты, которых нет в списке контактов на сервере
            if (!contactsToRemoveFromDb.isEmpty()) 
                contactsToRemoveFromDb.forEach(entry -> removeContactFromDb(entry));
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.getContacts_error", e);
        }

        // проверяем, есть ли наш пользователь в БД
        User user = dbService.getUser(myUser.getId());
        if (user == null) {
            dbService.insertUser(myUser);
        }
    }

    private void synchronizePageOfContListFromServ(ContactFromServer[] contacts, List<User> contactsToRemoveFromDb) {
        for (ContactFromServer entry : contacts) {
            User curUser = entry.toUser();
            if (!contactList.contains(curUser.getId())) {
                addContactToDB(curUser);
            }
            //todo в новом апи пока нет статусов
//            for (CFXListElement cont : contactListOfCards) {
//                if (cont.getUser().getId().equals(entry.getUserProfile().getId())) {
//                    cont.setOnlineStatus(entry.isStatusOnline());
//                    break;
//                }
//            }
            //в списке останутся только контакты, которых нет на сервере
            contactsToRemoveFromDb.remove(curUser);
        }
    }

    private User convertUserProfileJSONToUser(String jsonText) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return new User(null, gson.fromJson(jsonText, UserProfile.class));
    }

    private List<CFXListElement> convertJSONToCFXListElements(String jsonText) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        List<CFXListElement> res = new ArrayList<>();
        try {
            List<UserProfile> finded = gson.fromJson(jsonText,
                    new TypeToken<List<UserProfile>>(){}.getType());
            finded.forEach(user -> res.add(new CFXListElement(new User(null, user))));
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.getUserByNameOrEmail_JsonParsError", e);
        }
        return res.isEmpty() ? null : res;
    }

    public Group getGroupInfo(String groupName){
        Group group = new Group();
        try {
            ServerResponse response = HTTPSRequest.getGroupInfo(groupName,token);
            switch (response.getResponseCode()){
                case 200:
                    System.out.println("получение информации о группе");
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    group = gson.fromJson(response.getResponseJson(), Group.class);
                    return group;
                case 400:
                    System.out.println("Проблемы с токеном");
                    break;
                case 404:
                    System.out.println("группа не найдена");
                    break;
                case 500:
                    System.out.println("ошибка сервера");
                    break;
                default:
                    System.out.println("другая ошибка");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return group;
    }

    public void joinGroup(String groupName){
        Group group = getGroupInfo(groupName);
        addUserGroup(group.getGid(), myUser.getId().toString());
    }

    public void addGroup(String group_name){
        AddGroup addGroup = new AddGroup(group_name);
        String requestJSON = new Gson().toJson(addGroup);

        try {
            ServerResponse response = HTTPSRequest.addGroup(requestJSON, token);
            switch (response.getResponseCode()) {
                case 200:
                    showAlert("Группа" + group_name + "успешно создана", Alert.AlertType.INFORMATION);
                    //addContactToDB(convertJSONToUser(response.getResponseJson()));
                    //if (chatViewController != null) chatViewController.fillContactListView();
                    break;
                case 400:
                    showAlert("Ошибка запроса", Alert.AlertType.ERROR);
                    break;
                case 404:
                    showAlert("Невозможно создать группу с названием: " + group_name, Alert.AlertType.ERROR);
                    break;
                case 500:
                    showAlert("Ошибка сервера", Alert.AlertType.ERROR);
                    break;
                default:
                    showAlert("Общая ошибка!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.addGroup_error", e);
        }

    }

    public void addUserGroup(String group_id, String new_user_id) {
        AddUserGroup aug = new AddUserGroup(group_id, new_user_id);
        String requestJSON = new Gson().toJson(aug);

        try {
            ServerResponse response = HTTPSRequest.addUserGroup(requestJSON, token);
            switch (response.getResponseCode()) {
                case 200:
                    showAlert("Группа успешно добавлена", Alert.AlertType.INFORMATION);
                    //addContactToDB(convertJSONToUser(response.getResponseJson()));
                    //if (chatViewController != null) chatViewController.fillContactListView();
                    break;
                case 400:
                    showAlert("Ошибка запроса", Alert.AlertType.ERROR);
                    break;
                case 404:
                    showAlert("Группа не найдена или вы уже состоите в ней", Alert.AlertType.ERROR);
                    break;
                case 500:
                    showAlert("Ошибка сервера", Alert.AlertType.ERROR);
                    break;
                default:
                    showAlert("Общая ошибка!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.addUserGroup_error", e);
        }
    }

    public boolean addContact(User user) {
        //todo ответа сервера не предусмотрено => убрать return и добавить проброс ошибок
        UserToServer uts = new UserToServer(user.getId().toString(), user.getAccount_name());
        try {
            ServerResponse response = HTTPSRequest.addContact(uts.toJson(), token);
            switch (response.getResponseCode()) {
                case 200:
                    showAlert("Контакт " + user.getAccount_name() + " успешно добавлен", Alert.AlertType.INFORMATION);
                    User newUser = ContactFromServer.fromJson(response.getResponseJson()).toUser();
                    return addContactToDbAndChat(newUser);
                default:
                    //showAlert("Общая ошибка!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.addContact_error", e);
        }
        return false;
    }

    public List<CFXListElement> findContact(String contact) {
        try {
            ServerResponse response = HTTPSRequest.getUser(null, contact, token);
            switch (response.getResponseCode()) {
                case 200:
                    return convertJSONToCFXListElements(response.getResponseJson());
                case 404:
                   // showAlert("Пользователь с email: " + contact + " не найден", Alert.AlertType.ERROR);
                    break;
                case 409:
                  //  showAlert("Пользователь " + contact + " уже есть в списке ваших контактов", Alert.AlertType.ERROR);
                    break;
                default:
                    //showAlert("Общая ошибка!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.getUserByNameOrEmail_error", e);
        }
        return null;
    }

    private void addContactToDB(User contact) {
        dbService.insertUser(contact);
        contact = dbService.getUserByEmail(contact.getEmail());
        if (contact != null) {
            Long id = contact.getId();
            contactList.add(id);
            contactListOfCards.add(new CFXListElement(contact));
        }
    }

    private boolean addContactToDbAndChat(User contact) {
        addContactToDB(contact);
        if (chatViewController != null) {
            chatViewController.updateContactListView();
        }
        return true;
    }

    public boolean removeContact(User user) {
        //todo ответа сервера не предусмотрено => убрать return и добавить проброс ошибок
        try {
            ServerResponse response = HTTPSRequest.deleteContact(user.getId().toString(), token);
            switch (response.getResponseCode()) {
                case 200:
                    showAlert("Контакт " + user.getAccount_name() + " успешно удалён", 
                            Alert.AlertType.INFORMATION);
                    removeContactFromDbAndChat(user);
                    return true;
                default:
                    //showAlert("Общая ошибка!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.addContact_error", e);
        }
        return false;
    }

    public void clearMessagesWithUser(User contact) {
        if (!dbService.getChat(myUser, contact).isEmpty())
            dbService.deleteChat(myUser, contact);
    }

    private void removeContactFromDb(User contact) {
        clearMessagesWithUser(contact);
        dbService.deleteUser(contact);
        contactList.remove(contact.getId());
        contactListOfCards.remove(new CFXListElement(contact));
    }

    private void removeContactFromDbAndChat(User contact) {
        removeContactFromDb(contact);
        if (chatViewController != null) {
            chatViewController.updateContactListView();
            if (receiver.equals(contact)) chatViewController.clearMessageWebView();
        }
    }

    public boolean proceedRegister(String name, String password, String email) {
//        String requestJSON = "{" +
//                "\"email\": \"" + email + "\"," +
//                "\"password\": \"" + password + "\"," +
//                "\"name\": \"" + name + "\"" +
//                "}";
        RegistrationToServer registrationToServer = new RegistrationToServer(email, password, name);
        try {
            RegistrationFromServer registrationFromServer = HTTPSRequest.registration(registrationToServer);
            if (registrationFromServer != null) {
                UserPub userPub = registrationFromServer.getUser();
                String token = registrationFromServer.getToken();
                //TODO записать в бд
                if (userPub != null && token != null) {
                    User user = new User(userPub, token);
                    user.setPassword(password);
                    myUser = user;
                    dbService = new DataBaseService(myUser);
                    contactList = dbService.getAllUserId();
                    contactListOfCards = new ArrayList<>();
                    addContactToDB(user);
                    dbService.close();

                    return true;
                }
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.registration", e);
        }
        return false;
    }

    public boolean proceedLogIn(String login, String password) {
        return authentication(login, password);
    }

    public List<String> getAllUserNames() {
        return dbService.getAllUserNames();
    }

    public String proceedRestorePassword(String email) {
        String answer = "0";
        String requestJSON = "{" +
                "\"email\": \"" + email + "\"" +
                "}";
        try {
            answer = HTTPSRequest.restorePassword(requestJSON);
        } catch (Exception e) {
            controllerLogger.error("proceedRestorePassword_error", e);
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
            controllerLogger.error("proceedChangePassword_error", e);
        }
        return answer;
    }
}
