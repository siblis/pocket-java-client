package ru.geekbrains.pocket.messenger.client.controller;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.pocket.messenger.client.model.ServerResponse;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.ContactFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.ContactListFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.UserProfileFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.UserToServer;
import ru.geekbrains.pocket.messenger.client.utils.Converter;
import ru.geekbrains.pocket.messenger.client.utils.HTTPSRequest;
import ru.geekbrains.pocket.messenger.client.view.customFX.CFXListElement;
import ru.geekbrains.pocket.messenger.database.entity.User;

import static ru.geekbrains.pocket.messenger.client.controller.ClientController.token;
import static ru.geekbrains.pocket.messenger.client.utils.Common.showAlert;

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

public class ContactController {

    static final Logger controllerLogger = LogManager.getLogger(AuthController.class);
    
    ClientController clientCtrllr;

    ContactController(ClientController cc) {
        clientCtrllr = cc;
    }

    void synchronizeContactList() {
        clientCtrllr.dbService.setUserDB(clientCtrllr.myUser);
        clientCtrllr.contactList = clientCtrllr.dbService.getAllUserId();
        clientCtrllr.contactListOfCards = new ArrayList<>();
        
        if (!clientCtrllr.contactList.isEmpty()) {
            clientCtrllr.dbService.getAllUsers().forEach(user -> {
                clientCtrllr.contactListOfCards.add(new CFXListElement(user));
            });
            clientCtrllr.contactListOfCards.remove(clientCtrllr.myUser);
        }

        try {
            List<User> contactsToRemoveFromDb = clientCtrllr.dbService.getAllUsers();
            contactsToRemoveFromDb.remove(clientCtrllr.myUser);
            ServerResponse response;
            int pageOfContacts = 0;
            while (true) {
                response = HTTPSRequest.getContacts(token, pageOfContacts++);
                if (response.getResponseCode() != 200) break;
                ContactListFromServer clfs = Converter.toJavaObject(response.getResponseJson(),
                        ContactListFromServer.class);
                if (clfs.getData().length == 0) break;
                synchronizePageOfContListFromServ(clfs.getData(), contactsToRemoveFromDb);
                
                break; //todo: баг на сервере
            }

            //удаляем из локальной базы контакты, которых нет в списке контактов на сервере
            if (!contactsToRemoveFromDb.isEmpty()) 
                contactsToRemoveFromDb.forEach(entry -> 
                        clientCtrllr.contactService.removeContactFromDb(entry));
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.getContacts_error", e);
        }

        // проверяем, есть ли наш пользователь в БД
        User user = clientCtrllr.dbService.getUserById(clientCtrllr.myUser.getId());
        if (user == null) {
            clientCtrllr.dbService.insertUser(clientCtrllr.myUser);
        }
    }

    void synchronizePageOfContListFromServ(ContactFromServer[] contacts, List<User> contactsToRemoveFromDb) {
        for (ContactFromServer entry : contacts) {
            User curUser = entry.toUser();
            if (!clientCtrllr.contactList.contains(curUser.getId())) {
                clientCtrllr.contactService.addContactToDB(curUser);
            }
            //todo в новом апи пока нет статусов
//            for (CFXListElement cont : contactListOfCards) {
//                if (cont.getUser().getId().equals(entry.getUserProfile().getId())) {
//                    cont.setOnlineStatus(entry.isStatusOnline());
//                    break;
//                }
//            }
            //по окончанию синхронизации в списке останутся только контакты, которых нет на сервере
            contactsToRemoveFromDb.remove(curUser);
        }
    }

    void addContactToDB(User contact) {
        clientCtrllr.dbService.insertUser(contact);
//        contact = dbService.getUserByEmail(contact.getEmail());
//        if (contact != null) {
//            Long id = contact.getId();
        addContactToContactLists(contact);
//        }
    }

    void addContactToContactLists(User contact) {
        clientCtrllr.contactList.add(contact.getId());
        clientCtrllr.contactListOfCards.add(new CFXListElement(contact));
    }

    boolean addContactToDbAndChat(User contact) {
        addContactToDB(contact);
        if (clientCtrllr.chatViewController != null) {
            clientCtrllr.chatViewController.updateContactListView();
        }
        return true;
    }

    void removeContactFromDb(User contact) {
        clientCtrllr.messageService.clearMessagesWithUser(contact);
        clientCtrllr.dbService.deleteUser(contact);
        clientCtrllr.contactList.remove(contact.getId());
        clientCtrllr.contactListOfCards.remove(new CFXListElement(contact));
    }

    void removeContactFromDbAndChat(User contact) {
        removeContactFromDb(contact);
        if (clientCtrllr.chatViewController != null) {
            clientCtrllr.chatViewController.updateContactListView();
            if (clientCtrllr.receiver.equals(contact)) {
                clientCtrllr.chatViewController.clearMessageWebView();
                clientCtrllr.receiver = null;
            }
        }
    }

    User getFromServerUserByEmail(String email) {
        try {
            ServerResponse response = HTTPSRequest.getUser(null, email, token);
            //todo: если от сервера ответ только с кодом 200, то убрать ненужное
            switch (response.getResponseCode()) {
                case 200:
                    UserProfileFromServer finded = Converter.toJavaObject(response.getResponseJson(),
                            UserProfileFromServer.class);
                    if (finded != null && !finded.isEmpty())
                        return new User(email, finded.toUserProfile());
                    break;
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

    User getFromServerUserById(String id) {
        try {
            ServerResponse response = HTTPSRequest.getUser(id, null, token);
            switch (response.getResponseCode()) {
                case 200:
                    UserProfileFromServer finded = Converter.toJavaObject(response.getResponseJson(),
                            UserProfileFromServer.class);
                    if (finded != null && !finded.isEmpty())
                        return new User(null, finded.toUserProfile());
                    break;
                case 404:
//                    showAlert("Пользователь с email: " + contact + " не найден", Alert.AlertType.ERROR);
                    break;
                default:
//                    showAlert("Общая ошибка!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.getUserByNameOrEmail_error", e);
        }
        return null;
    }

    CFXListElement findContact(String contact) {
        User finded = getFromServerUserByEmail(contact);
        CFXListElement le = finded == null ? null : new CFXListElement(finded);
        if (le != null) le.setBody(finded.getEmail());
        return le;
    }

    boolean addContact(User user) {
        //todo ответа сервера не предусмотрено => убрать return и добавить проброс ошибок
        UserToServer uts = new UserToServer(user.getId(), user.getUserName());
        try {
            ServerResponse response = HTTPSRequest.addContact(uts.toJson(), token);
            switch (response.getResponseCode()) {
                case 200:
                    showAlert("Контакт " + user.getUserName() + " успешно добавлен", Alert.AlertType.INFORMATION);
                    ContactFromServer newCont = Converter.toJavaObject(response.getResponseJson(),
                            ContactFromServer.class);
                    if (addContactToDbAndChat(newCont.toUser()))
                        return true;
                    break;
                default:
                    //showAlert("Общая ошибка!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.addContact_error", e);
        }
        return false;
    }

    boolean removeContact(User user) {
        //todo ответа сервера не предусмотрено => убрать return и добавить проброс ошибок
        try {
            ServerResponse response = HTTPSRequest.deleteContact(user.getId(), token);
            switch (response.getResponseCode()) {
                case 200:
                    removeContactFromDbAndChat(user);
                    showAlert("Контакт " + user.getUserName() + " успешно удалён", 
                            Alert.AlertType.INFORMATION);
                    return true;
                default:
                    //showAlert("Общая ошибка!", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.addContact_error", e);
        }
        return false;
    }
}
