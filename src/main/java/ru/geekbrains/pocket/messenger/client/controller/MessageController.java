package ru.geekbrains.pocket.messenger.client.controller;

import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.pocket.messenger.client.model.ServerResponse;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.MessageFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.MessageListFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.MessageToServer;
import ru.geekbrains.pocket.messenger.client.utils.Converter;
import ru.geekbrains.pocket.messenger.client.utils.HTTPSRequest;
import ru.geekbrains.pocket.messenger.client.utils.Sound;
import ru.geekbrains.pocket.messenger.database.entity.Message;
import ru.geekbrains.pocket.messenger.database.entity.User;

import java.io.IOException;
import java.sql.Timestamp;
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

    static final Logger controllerLogger = LogManager.getLogger(AuthController.class);
    
    ClientController clientCtrllr;

    MessageController(ClientController cc) {
        clientCtrllr = cc;
    }

    void loadChat() {
        List<Message> converstation = clientCtrllr.dbService.getChat(clientCtrllr.myUser, clientCtrllr.receiver);
        clientCtrllr.chatViewController.clearMessageWebView();
        for (Message message : converstation) {
            clientCtrllr.chatViewController.showMessage(message, false);
        }
    }

    void receiveMessage(String message) {
        MessageFromServer mfs = Converter.toJavaObject(message, MessageFromServer.class);
        //todo: доделать логику на получение уведомлений о прочтении отправленного сообщения!?
        //todo: доработать логику получения сообщения из группы
        //Проверяем, что осообщение пришло не от клиента в списке
        if (!clientCtrllr.contactList.contains(mfs.getSender())) {
            User newCont = clientCtrllr.contactService.getFromServerUserById(mfs.getSender());
            if (newCont != null)
                clientCtrllr.contactService.addContact(newCont);
            else
                controllerLogger.error("Получено сообщение от пользователя, данных которого " +
                        "нет на сервере. Сообщение:\n" + message);
        }
        //Проверяем что у нас чат именно с этим пользователем, иначе сообщение не выводится
        if (clientCtrllr.receiver.getId().equals(mfs.getSender())) {
            Message mess = mfs.toMessageWithoutUsers();
            mess.setSender(clientCtrllr.dbService.getUserById(mfs.getSender()));
            mess.setReceiver(clientCtrllr.dbService.getUserById(mfs.getRecipient()));
            clientCtrllr.chatViewController.showMessage(mess, true);
            clientCtrllr.dbService.addMessage(mess);
        }
        if (mfs.getSender() != null) { //отключаем звук для служебных сообщений
            Sound.playSoundNewMessage().join(); //Звук нового сообщения должен быть в любом случае
        }
    }

    void sendMessage(String message) {
        if (clientCtrllr.receiver == null) {
            showAlert("Выберите контакт для отправки сообщения", Alert.AlertType.ERROR);
            return;
        }
        
        String jsonMessage = Converter.toJson(
                new MessageToServer(message, null, clientCtrllr.receiver.getId(), null));
        try {
            clientCtrllr.conn.getChatClient().send(jsonMessage);
            //todo допилить получение Success/Error и MessageId из ответа

            Message mess = new Message();
            mess.setReceiver(clientCtrllr.receiver);
            mess.setSender(clientCtrllr.myUser);
            mess.setText(message);
            mess.setTime(new Timestamp(System.currentTimeMillis()));

            clientCtrllr.dbService.addMessage(mess);

            clientCtrllr.chatViewController.showMessage(mess, false);

        } catch (IOException ex) {
            showAlert("Потеряно соединение с сервером", Alert.AlertType.ERROR);
            controllerLogger.error(ex);
        }

    }

    void clearMessagesWithUser(User contact) {
        if (!clientCtrllr.dbService.getChat(clientCtrllr.myUser, contact).isEmpty())
            clientCtrllr.dbService.deleteChat(clientCtrllr.myUser, contact);
    }

    public void getChatWithUser(String contactId) {
        try {
            User contact = clientCtrllr.dbService.getUserById(contactId);
            List<Message> messageListFromDb = clientCtrllr.dbService.getChat(clientCtrllr.myUser, contact);
            int pageOfMessages = 0;
            while (true) {
                ServerResponse response = HTTPSRequest.getUserMessages(token, contactId, pageOfMessages++);
                if (response.getResponseCode() != 200) break;
                MessageListFromServer mlfs = Converter.toJavaObject(response.getResponseJson(), MessageListFromServer.class);
                if (mlfs.getData().length == 0) break;
                synchronizeMessageListFromServ(mlfs.getData(), messageListFromDb);
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.getContacts_error", e);
        }
    }

    private void synchronizeMessageListFromServ(MessageFromServer[] messages, List<Message> messageListFromDb) {
        clientCtrllr.chatViewController.clearMessageWebView();
        for (MessageFromServer entry : messages) {
            Message mess = entry.toMessageWithoutUsers();
            if (!messageListFromDb.contains(mess.getId())) {
                mess.setSender(clientCtrllr.dbService.getUserById(entry.getSender()));
                mess.setReceiver(clientCtrllr.dbService.getUserById(entry.getRecipient()));
                clientCtrllr.dbService.addMessage(mess);
                clientCtrllr.chatViewController.showMessage(mess, false);
            }
        }
    }
}
