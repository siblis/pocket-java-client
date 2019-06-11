package ru.geekbrains.pocket.messenger.client.controller;

import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompSession;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.MessageFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.MessageListFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.MessageToServer;
import ru.geekbrains.pocket.messenger.client.utils.HTTPSRequest;
import ru.geekbrains.pocket.messenger.client.utils.Sound;
import ru.geekbrains.pocket.messenger.database.entity.Message;
import ru.geekbrains.pocket.messenger.database.entity.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.geekbrains.pocket.messenger.client.controller.ClientController.token;
import static ru.geekbrains.pocket.messenger.client.utils.Common.showAlert;

//for api:
//User messages
//          /user/%id/messages
//          /user/%id/messages/%id
//Groups messages
//          /groups/%id/messages
//          /groups/%id/messages/%id

public class MessageController {

    static final Logger controllerLogger = LogManager.getLogger(MessageController.class);
    public static final int PAGE_SIZE = 50;

    ClientController cc;
    private StompSession session;
    private MessageToServer waitForConfirm;

    MessageController(ClientController cc) {
        this.cc = cc;
    }

    public StompSession getSession() {
        return session;
    }

    public void setSession(StompSession session) {
        this.session = session;
    }

    public void resetWaitForConfirm() {
        this.waitForConfirm = null;
    }

    void loadChat() {
        if (!cc.isChatUpdated.contains(cc.receiver.getId())) {
            cc.conversation = cc.dbService.getChat(cc.myUser, cc.receiver);
            getChatWithUser();
        }
        cc.conversation = cc.dbService.getChat(cc.myUser, cc.receiver, 0);
        Collections.reverse(cc.conversation);
        cc.chatViewController.clearMessageWebView();
        for (Message message : cc.conversation) {
            cc.chatViewController.showMessage(message, false);
        }
        cc.chatViewController.addJSBridgeToWebView();
    }

    synchronized void receiveMessage(MessageFromServer mfs) {
        //todo: доделать логику на получение уведомлений о прочтении отправленного сообщения!?
        //todo: доработать логику получения сообщения из группы
        //Проверяем, что осообщение пришло не от клиента в списке
        if (!cc.contactList.contains(mfs.getSender())) {
            User newCont = cc.contactService.getFromServerUserById(mfs.getSender());
            if (newCont != null)
                cc.contactService.addContact(newCont);
            else
                controllerLogger.error("Получено сообщение от пользователя, данных которого " +
                        "нет на сервере. Сообщение:\n" + mfs);
        }
        Message mess = mfs.toMessageWithoutUsers();
        mess.setSender(cc.dbService.getUserById(mfs.getSender()));
        mess.setReceiver(cc.dbService.getUserById(mfs.getRecipient()));
        cc.dbService.addMessage(mess);
        //Проверяем что у нас чат именно с этим пользователем, иначе сообщение не выводится
        if (cc.receiver != null && cc.receiver.getId().equals(mfs.getSender())) {
            cc.chatViewController.showMessage(mess, true);
        } else {
            //todo: превью и счётчик непрочитанных для контакта / группы, от которых пришло сообщение
        }
        if (mfs.getSender() != null || mfs.getGroup() != null) { //отключаем звук для служебных сообщений
            Sound.playSoundNewMessage().join(); //Звук нового сообщения должен быть в любом случае
        }
    }

    void sendMessage(String message) {
        if (cc.receiver == null) {
            showAlert("Выберите контакт для отправки сообщения", Alert.AlertType.ERROR);
            return;
        }

        MessageToServer mts = new MessageToServer(message, null, cc.receiver.getId(), null);
        session.send("/v1/send", mts);
        waitForConfirm = mts;
    }

    public void saveToDBAndShowMessage(String messageId) {
            Message mess = new Message();
            mess.setId(messageId);
            mess.setReceiver(cc.receiver);
            mess.setSender(cc.myUser);
            mess.setText(waitForConfirm.getText());
            mess.setTime(new Timestamp(System.currentTimeMillis()));

            cc.dbService.addMessage(mess);

            cc.chatViewController.showMessage(mess, false);
    }

    void clearMessagesWithUser(User contact) {
        if (!cc.dbService.getChat(cc.myUser, contact).isEmpty())
            cc.dbService.deleteChat(cc.myUser, contact);
    }

    private void getChatWithUser() {
        try {
            int pageOfMessages = 0;
            while (true) {
                String receiverId = cc.receiver.getId();
                int responseCode = HTTPSRequest.sendRequest("/user/" + receiverId +
                        "/messages?offset=" + pageOfMessages++, "GET", null, token);
                if (responseCode != 200) break;
                MessageListFromServer mlfs = HTTPSRequest.getResponse(MessageListFromServer.class);
                if (mlfs.getData().length == 0) {
                    cc.isChatUpdated.add(receiverId);
                    break;
                }
                synchronizeMessageListFromServ(mlfs.getData());
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.getContacts_error", e);
        }
    }

    private void synchronizeMessageListFromServ(MessageFromServer[] messages) {
        List<String> messageListFromDbId = new ArrayList<>();
        for (Message message : cc.conversation) {
            messageListFromDbId.add(message.getId());
        }
        for (MessageFromServer entry : messages) {
            Message mess = entry.toMessageWithoutUsers();
            if (!messageListFromDbId.contains(mess.getId())) {
                mess.setSender(cc.dbService.getUserById(entry.getSender()));
                mess.setReceiver(cc.dbService.getUserById(entry.getRecipient()));
                cc.dbService.addMessage(mess);
                cc.conversation.add(mess);
            }
        }
    }

    synchronized void loadPreviousPageOfMessages() {
        int currentPageNumber = (cc.chatViewController.getIdMsg() - 1) / PAGE_SIZE;
        cc.conversation = cc.dbService.getChat(cc.myUser, cc.receiver, ++currentPageNumber);
        if (cc.conversation.size() == 0) {
            cc.chatViewController.setTopOfConversation(true);
        } else {
            cc.chatViewController.removeDateOnTop();
            for (Message message : cc.conversation) {
                cc.chatViewController.showMessageOnTop(message);
            }
            cc.chatViewController.showDateOnTop();
        }
    }
}
