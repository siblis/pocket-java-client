package ru.geekbrains.pocket.messenger.client.controller;

import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompSession;
import ru.geekbrains.pocket.messenger.client.model.ServerResponse;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.MessageFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.MessageListFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.MessageToServer;
import ru.geekbrains.pocket.messenger.client.utils.Converter;
import ru.geekbrains.pocket.messenger.client.utils.HTTPSRequest;
import ru.geekbrains.pocket.messenger.client.utils.Sound;
import ru.geekbrains.pocket.messenger.database.entity.Message;
import ru.geekbrains.pocket.messenger.database.entity.User;

import java.sql.Timestamp;
import java.util.ArrayList;
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
    
    ClientController clientCtrllr;
    private StompSession session;
    private MessageToServer waitForConfirm;

    MessageController(ClientController cc) {
        clientCtrllr = cc;
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
        clientCtrllr.conversation = clientCtrllr.dbService.getChat(clientCtrllr.myUser, clientCtrllr.receiver);
        getChatWithUser();
        clientCtrllr.chatViewController.clearMessageWebView();
        for (Message message : clientCtrllr.conversation) {
            clientCtrllr.chatViewController.showMessage(message, false);
        }
    }

    void receiveMessage(MessageFromServer mfs) {
        //todo: доделать логику на получение уведомлений о прочтении отправленного сообщения!?
        //todo: доработать логику получения сообщения из группы
        //Проверяем, что осообщение пришло не от клиента в списке
        if (!clientCtrllr.contactList.contains(mfs.getSender())) {
            User newCont = clientCtrllr.contactService.getFromServerUserById(mfs.getSender());
            if (newCont != null)
                clientCtrllr.contactService.addContact(newCont);
            else
                controllerLogger.error("Получено сообщение от пользователя, данных которого " +
                        "нет на сервере. Сообщение:\n" + mfs);
        }
        Message mess = mfs.toMessageWithoutUsers();
        mess.setSender(clientCtrllr.dbService.getUserById(mfs.getSender()));
        mess.setReceiver(clientCtrllr.dbService.getUserById(mfs.getRecipient()));
        clientCtrllr.dbService.addMessage(mess);
        //Проверяем что у нас чат именно с этим пользователем, иначе сообщение не выводится
        if (clientCtrllr.receiver != null && clientCtrllr.receiver.getId().equals(mfs.getSender())) {
            clientCtrllr.chatViewController.showMessage(mess, true);
        } else {
            //todo: превью и счётчик непрочитанных для контакта / группы, от которых пришло сообщение
        }
        if (mfs.getSender() != null || mfs.getGroup() != null) { //отключаем звук для служебных сообщений
            Sound.playSoundNewMessage().join(); //Звук нового сообщения должен быть в любом случае
        }
    }

    void sendMessage(String message) {
        if (clientCtrllr.receiver == null) {
            showAlert("Выберите контакт для отправки сообщения", Alert.AlertType.ERROR);
            return;
        }

        MessageToServer mts = new MessageToServer(message, null, clientCtrllr.receiver.getId(), null);
        session.send("/v1/send", mts);
        waitForConfirm = mts;
    }

    public void saveToDBAndShowMessage(String messageId) {
            Message mess = new Message();
            mess.setId(messageId);
            mess.setReceiver(clientCtrllr.receiver);
            mess.setSender(clientCtrllr.myUser);
            mess.setText(waitForConfirm.getText());
            mess.setTime(new Timestamp(System.currentTimeMillis()));

            clientCtrllr.dbService.addMessage(mess);

            clientCtrllr.chatViewController.showMessage(mess, false);
    }

    void clearMessagesWithUser(User contact) {
        if (!clientCtrllr.dbService.getChat(clientCtrllr.myUser, contact).isEmpty())
            clientCtrllr.dbService.deleteChat(clientCtrllr.myUser, contact);
    }

    private void getChatWithUser() {
        try {
            int pageOfMessages = 0;
            while (true) {
                ServerResponse response = HTTPSRequest.getUserMessages(token, clientCtrllr.receiver.getId(), pageOfMessages++);
                if (response.getResponseCode() != 200) break;
                MessageListFromServer mlfs = Converter.toJavaObject(response.getResponseJson(), MessageListFromServer.class);
                if (mlfs.getData().length == 0) break;
                synchronizeMessageListFromServ(mlfs.getData());
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.getContacts_error", e);
        }
    }

    private void synchronizeMessageListFromServ(MessageFromServer[] messages) {
        List<String> messageListFromDbId = new ArrayList<>();
        for (Message message : clientCtrllr.conversation) {
            messageListFromDbId.add(message.getId());
        }
        for (MessageFromServer entry : messages) {
            Message mess = entry.toMessageWithoutUsers();
            if (!messageListFromDbId.contains(mess.getId())) {
                mess.setSender(clientCtrllr.dbService.getUserById(entry.getSender()));
                mess.setReceiver(clientCtrllr.dbService.getUserById(entry.getRecipient()));
                clientCtrllr.dbService.addMessage(mess);
            }
        }
        clientCtrllr.conversation = clientCtrllr.dbService.getChat(clientCtrllr.myUser, clientCtrllr.receiver);
    }
}
