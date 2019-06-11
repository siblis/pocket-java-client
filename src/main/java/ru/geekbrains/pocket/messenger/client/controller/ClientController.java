package ru.geekbrains.pocket.messenger.client.controller;


import javafx.application.HostServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompSession;
import ru.geekbrains.pocket.messenger.client.model.Group;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.MessageFromServer;
import ru.geekbrains.pocket.messenger.client.utils.Connector;
import ru.geekbrains.pocket.messenger.client.view.ChatViewController;
import ru.geekbrains.pocket.messenger.client.view.customFX.CFXListElement;
import ru.geekbrains.pocket.messenger.database.dao.DataBaseService;
import ru.geekbrains.pocket.messenger.database.entity.Message;
import ru.geekbrains.pocket.messenger.database.entity.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClientController {
    static final Logger controllerLogger = LogManager.getLogger(ClientController.class);
    static ClientController instance;
    static String token;
    ChatViewController chatViewController;

    User receiver;
    User myUser;
    Connector conn;
    List<String> contactList;
    List<CFXListElement> contactListOfCards;
    List<Message> conversation;
    Set<String> isChatUpdated;

    DataBaseService dbService;
    
    AuthController authService;
    ContactController contactService;
    GroupController groupService;
    MessageController messageService;

    private HostServices hostServices;

    public HostServices getHostServices() {
        return hostServices;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void setChatViewController(ChatViewController chatViewController) {
        this.chatViewController = chatViewController;
    }

    private ClientController() {
        receiver = null;
        myUser = null;
        conn = null;
        dbService = new DataBaseService();
        isChatUpdated = new HashSet<>();
        authService = new AuthController(this);
        contactService = new ContactController(this);
        groupService = new GroupController(this);
        messageService = new MessageController(this);
    }

    public static ClientController getInstance() {
        if (instance == null) {
            instance = new ClientController();
        }
        return instance;
    }

    public boolean proceedRegister(String name, String password, String email) {
        return authService.registration(name, password, email);
    }

    public boolean proceedLogIn(String login, String password) {
        return authService.login(login, password);
    }

    public String proceedChangePassword(String email, String codeRecovery, String password) {
        return authService.changePassword(email, codeRecovery, password);
    }

    public String proceedRestorePassword(String email) {
        return authService.restorePassword(email);
    }

    public void disconnect() {
        if (conn != null) {
            messageService.getSession().disconnect();
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
        conversation = null;
        authService = null;
        contactService = null;
        groupService = null;
        messageService = null;
    }

    public List<String> getAllUserNames() {
        return dbService.getAllUserNames();
    }

    public User getMyUser() {
        return myUser;
    }

    public User getMyUserUpdatedFromServer() {
        return getUserUpdatedFromServer(myUser);
    }

    public User getUserUpdatedFromServer(String id) {
        if (receiver.getId().equals(id)) {
            return getUserUpdatedFromServer(receiver);
        } else {
            return getUserUpdatedFromServer(dbService.getUserById(id));
        }
    }

    private User getUserUpdatedFromServer(User toUpdate) {
        User updateFromServer;
        if (myUser.getId().equals(toUpdate.getId())) {
            updateFromServer = contactService.getMyUserFromServer();
        } else {
            updateFromServer = contactService.getFromServerUserById(toUpdate.getId());
        }
        if (updateFromServer != null && toUpdate.updateUserInfo(updateFromServer)) {
            controllerLogger.info("User data updated:\n\t" + dbService.getUserById(toUpdate.getId()));
        }
        return toUpdate;
    }

    public User getReciever() {
        return receiver;
    }

    public String getSenderName() {
        return myUser.getUserName();
    }

    public String getRecieverName() {
        return receiver.getUserName();
    }

    public void setReceiver(String receiverId) {
        this.receiver = dbService.getUserById(receiverId);
        messageService.loadChat();
    }

    public void setReceiver(User receiver) {
        if (!contactList.contains(receiver.getId()))
            contactService.addContactToDbAndChat(receiver); // todo поправить логику получения сообщений?
            //NB: todo добавлять в список на сервере? addContactToDbAndChat не добавляет
        this.receiver = receiver;
        messageService.loadChat();
    }

    public boolean hasUserInLocalDB(String userId) {
        return dbService.getUserById(userId) != null;
    }

    public void setMessageSession(StompSession session) {
        messageService.setSession(session);
    }

    public List<CFXListElement> getContactListOfCards() {
        return contactListOfCards;
    }

    public boolean addContact(User user) {
        return contactService.addContact(user);
    }

    public CFXListElement findContact(String contact) {
        return contactService.findContact(contact);
    }

    public boolean removeContact(User user) {
        return contactService.removeContact(user);
    }

    public void receiveMessage(MessageFromServer message) {
        messageService.receiveMessage(message);
    }

    public void sendMessage(String message) {
        messageService.sendMessage(message);
    }

    public void clearMessagesWithUser(User contact) {
        messageService.clearMessagesWithUser(contact);
    }

    public Group getGroupInfo(String groupName){
        return groupService.getGroupInfo(groupName);
    }

    public void joinGroup(String groupName){
        groupService.joinGroup(groupName);
    }

    public void addGroup(String group_name, String group_desc){
        groupService.addGroup(group_name, group_desc);
    }

    public void addUserGroup(String group_id, String new_user_id) {
        groupService.addUserGroup(group_id, new_user_id);
    }

    public void resetWaitForConfirm() {
        messageService.resetWaitForConfirm();
    }

    public void saveToDBAndShowMessage(String s) {
        messageService.saveToDBAndShowMessage(s);
    }

    public void loadPreviousPageOfMessages() {
        messageService.loadPreviousPageOfMessages();
    }

    public void clearIsChatUpdatedSet() {
        isChatUpdated.clear();
    }
}
