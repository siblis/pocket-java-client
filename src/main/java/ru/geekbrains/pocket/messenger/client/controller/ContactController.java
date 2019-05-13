package ru.geekbrains.pocket.messenger.client.controller;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.ContactFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.ContactListFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.UserProfileFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.UserToServer;
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

    static final Logger controllerLogger = LogManager.getLogger(ContactController.class);

    ClientController cc;

    ContactController(ClientController cc) {
        this.cc = cc;
    }

    void synchronizeContactList() {
        cc.contactList = getAllContactId();
        try {
            List<User> contactsToRemoveFromDb = getAllContacts();
            int responseCode;
            int pageOfContacts = 0;
            while (true) {
                responseCode = HTTPSRequest.sendRequest("/account/contacts/?offset=" + pageOfContacts++,
                        "GET", null, token);
                if (responseCode != 200) break;
                ContactListFromServer clfs = HTTPSRequest.getResponse(ContactListFromServer.class);
                if (clfs.getData().length == 0) break;
                synchronizePageOfContListFromServ(clfs.getData(), contactsToRemoveFromDb);
            }

            //удаляем из локальной базы контакты, которых нет в списке контактов на сервере
            if (!contactsToRemoveFromDb.isEmpty())
                contactsToRemoveFromDb.forEach(entry -> removeContactFromDb(entry));

            cc.contactListOfCards = new ArrayList<>();
            if (!cc.contactList.isEmpty()) {
                getAllContacts().forEach(
                        user -> cc.contactListOfCards.add(new CFXListElement(user)));
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.getContacts_error", e);
        }
    }

    private void synchronizePageOfContListFromServ(ContactFromServer[] contacts, List<User> contactsToRemoveFromDb) {
        for (ContactFromServer entry : contacts) {
            User curUser = entry.toUser();
            if (!cc.contactList.contains(curUser.getId())) {
                addContactToDb(curUser);
            } else if (contactsToRemoveFromDb.contains(curUser)) {
                contactsToRemoveFromDb.remove(curUser);
            } else {
                contactsToRemoveFromDb.remove(cc.dbService.getUserById(curUser.getId()));
                cc.dbService.updateUser(curUser);
            }
        }
    }

    private List<String> getAllContactId() {
        List<String> contacts = cc.dbService.getAllUserId();
        contacts.remove(cc.myUser.getId());
        return contacts;
    }

    private List<User> getAllContacts() {
        List<User> contacts = cc.dbService.getAllUsers();
        contacts.remove(cc.myUser);
        return contacts;
    }

    boolean addContactToDb(User contact) {
        cc.dbService.insertUser(contact);
        cc.contactList.add(contact.getId());
        return true;
    }

    boolean addContactToDbAndChat(User contact) {
        addContactToDb(contact);
        cc.contactListOfCards.add(new CFXListElement(contact));
        if (cc.chatViewController != null) {
            cc.chatViewController.updateContactListView();
        }
        return true;
    }

    void removeContactFromDb(User contact) {
        cc.messageService.clearMessagesWithUser(contact);
        cc.dbService.deleteUser(contact);
        cc.contactList.remove(contact.getId());
    }

    void removeContactFromDbAndChat(User contact) {
        removeContactFromDb(contact);
        cc.contactListOfCards.remove(new CFXListElement(contact));
        if (cc.chatViewController != null) {
            cc.chatViewController.updateContactListView();
            if (cc.receiver.equals(contact)) {
                cc.chatViewController.clearMessageWebView();
                cc.receiver = null;
            }
        }
    }

    User getFromServerUserByEmail(String email) {
        try {
            int responseCode = HTTPSRequest.sendRequest("/users?email=" + email, "GET", null, token);
            if (responseCode == 200) {
                UserProfileFromServer finded = HTTPSRequest.getResponse(UserProfileFromServer.class);
                if (finded != null && !finded.isEmpty())
                    return new User(email, finded.toUserProfile());
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.getUserByEmail_error", e);
        }
        return null;
    }

    User getFromServerUserById(String id) {
        try {
            int responseCode = HTTPSRequest.sendRequest("/users/" + id, "GET", null, token);
            if (responseCode == 200) {
                UserProfileFromServer finded = HTTPSRequest.getResponse(UserProfileFromServer.class);
                if (finded != null && !finded.isEmpty())
                    return new User(null, finded.toUserProfile());
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.getUserById_error", e);
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
            int responseCode = HTTPSRequest.sendRequest("/account/contacts/", "POST", uts.toJson(), token);
            if (responseCode == 200) {
                showAlert("Контакт " + user.getUserName() + " успешно добавлен", Alert.AlertType.INFORMATION);
                ContactFromServer newCont = HTTPSRequest.getResponse(ContactFromServer.class);
                if (newCont != null && addContactToDbAndChat(newCont.toUser()))
                    return true;
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.addContact_error", e);
        }
        return false;
    }

    boolean removeContact(User user) {
        //todo ответа сервера не предусмотрено => убрать return и добавить проброс ошибок
        int responseCode = 0;
        try {
            responseCode = HTTPSRequest.sendRequest("/account/contacts/" + user.getId(), "DELETE", null, token);
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.addContact_error", e);
        }
        if (responseCode == 200) {
            removeContactFromDbAndChat(user);
            showAlert("Контакт " + user.getUserName() + " успешно удалён", Alert.AlertType.INFORMATION);
                return true;
        }
        return false;
    }
}
