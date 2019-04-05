package ru.geekbrains.pocket.messenger.client.controller;

//for api:
//Account
//          /account/
//Contacts
//          /account/contacts/
//          /account/contacts/%id
//Blacklist
//          /account/blacklist/
//          /account/blacklist/%id
//User
//          /users/
//          /users/%id

import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.pocket.messenger.client.model.ServerResponse;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.ContactFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.ContactListFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.UserToServer;
import ru.geekbrains.pocket.messenger.client.utils.Converter;
import ru.geekbrains.pocket.messenger.client.utils.HTTPSRequest;
import ru.geekbrains.pocket.messenger.client.view.ChatViewController;
import ru.geekbrains.pocket.messenger.client.view.customFX.CFXListElement;
import ru.geekbrains.pocket.messenger.database.dao.DataBaseService;
import ru.geekbrains.pocket.messenger.database.entity.Message;
import ru.geekbrains.pocket.messenger.database.entity.User;

import java.util.ArrayList;
import java.util.List;

import static ru.geekbrains.pocket.messenger.client.utils.Common.showAlert;

public class ContactController {
    private static final Logger log = LogManager.getLogger(ContactController.class);
    private static ContactController instance;
    private DataBaseService dbService;
    private List<String> contactList;
    private User receiver;
    private List<CFXListElement> contactListOfCards;
    private ChatViewController chatViewController;
    private ClientController clientController;

    public ContactController(){
        chatViewController = ChatViewController.getInstance();
        clientController = ClientController.getInstance();
        dbService = DataBaseService.getInstance();
    }

    public static ContactController getInstance() {
        if (instance == null) {
            instance = new ContactController();
        }
        return instance;
    }

    public List<String> getContactList() {
        return contactList;
    }

    public List<CFXListElement> getContactListOfCards() {
        return contactListOfCards;
    }

    public void setChatViewController(ChatViewController chatViewController) {
        this.chatViewController = chatViewController;
    }

    void synchronizeContactList(User myUser, String token) {
        //dbService = new DataBaseService(myUser);
        dbService.setUserDB(myUser);
        contactList = dbService.getAllUserUid();
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
                response = HTTPSRequest.getContacts(token, pageOfContacts++);
                ContactListFromServer clfs = ContactListFromServer.fromJson(response.getResponseJson());
                if (clfs.getContacts().length == 0) break;
                synchronizePageOfContListFromServ(clfs.getContacts(), contactsToRemoveFromDb);

                //заглушка, пока на сервере не реализована постраничная отправка списка контактов
                break;
            }

            //удаляем из локальной базы контакты, которых нет в списке контактов на сервере
            if (!contactsToRemoveFromDb.isEmpty())
                contactsToRemoveFromDb.forEach(entry -> removeContactFromDb(entry));
        } catch (Exception e) {
            log.error("HTTPSRequest.getContacts_error", e);
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
            if (!contactList.contains(curUser.getUid())) {
                addContactToDB(curUser);
            } else contactListOfCards.add(new CFXListElement(curUser));
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

    public boolean addContact(User user) {
        //todo ответа сервера не предусмотрено => убрать return и добавить проброс ошибок
        UserToServer uts = new UserToServer(user.getUid(), user.getAccount_name());
        try {
            ServerResponse response = HTTPSRequest.addContact(uts.toJson(), clientController.getToken());
            switch (response.getResponseCode()) {
                case 200:
                    showAlert("Контакт " + user.getAccount_name() + " успешно добавлен", Alert.AlertType.INFORMATION);
                    User newUser = ContactFromServer.fromJson(response.getResponseJson()).toUser();
                    return addContactToDbAndChat(newUser);
                default:
                    //showAlert("Общая ошибка!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            log.error("HTTPSRequest.addContact_error", e);
        }
        return false;
    }

    public CFXListElement findContact(String contact, String token) {
        try {
            ServerResponse response = HTTPSRequest.getUser(null, contact, token);
            switch (response.getResponseCode()) {
                case 200:
                    return Converter.convertJSONToCFXListElement(response.getResponseJson());
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
            log.error("HTTPSRequest.getUserByNameOrEmail_error", e);
        }
        return null;
    }

    private void addContactToDB(User contact) {
        dbService.insertUser(contact);
//        contact = dbService.getUserByEmail(contact.getEmail());
//        if (contact != null) {
//            Long id = contact.getId();
        addContactToContactLists(contact);
//        }
    }

    private void addContactToContactLists(User contact) {
        contactList.add(contact.getUid());
        contactListOfCards.add(new CFXListElement(contact));
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
            ServerResponse response = HTTPSRequest.deleteContact(user.getUid(), clientController.getToken());
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
            log.error("HTTPSRequest.addContact_error", e);
        }
        return false;
    }

    public void clearMessagesWithUser(User contact) {
        if (!dbService.getChat(clientController.getMyUser(), contact).isEmpty())
            dbService.deleteChat(clientController.getMyUser(), contact);
    }

    private void removeContactFromDb(User contact) {
        clearMessagesWithUser(contact);
        dbService.deleteUser(contact);
        contactList.remove(contact.getUid());
        contactListOfCards.remove(new CFXListElement(contact));
    }

    private void removeContactFromDbAndChat(User contact) {
        removeContactFromDb(contact);
        if (chatViewController != null) {
            chatViewController.updateContactListView();
            if (receiver.equals(contact)) chatViewController.clearMessageWebView();
        }
    }

    private void loadChat(User receiver) {
        List<Message> converstation = dbService.getChat(clientController.getMyUser(), receiver);
        chatViewController.clearMessageWebView();

        for (Message message :
                converstation) {
            chatViewController.showMessage(message.getSender().getAccount_name(), message.getText(), message.getTime(), false);
        }
    }

    public String getRecieverName() {
        return receiver.getAccount_name();
    }

    public void setReceiver(long receiverId) {
        this.receiver = dbService.getUser(receiverId);
        loadChat(receiver);
    }

    public void setReceiver(String receiverUid) {
        this.receiver = dbService.getUserbyUid(receiverUid);
        loadChat(receiver);
    }

    public void setReceiver(User receiver) {
        if (!contactList.contains(receiver.getUid()))
            addContactToDbAndChat(receiver); // todo поправить логику получения сообщений?
        //NB: todo добавлять в список на сервере? addContactToDbAndChat не добавляет
        this.receiver = receiver;
        loadChat(receiver);
    }

    public User getReciever() {
        return receiver;
    }

    public boolean hasReceiver(long receiverId) {
        return dbService.getUser(receiverId) != null;
    }

    public boolean hasReceiver(String receiverUid) {
        return dbService.getUserbyUid(receiverUid) != null;
    }

}
