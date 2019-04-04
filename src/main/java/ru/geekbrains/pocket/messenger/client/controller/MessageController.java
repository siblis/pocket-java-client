package ru.geekbrains.pocket.messenger.client.controller;

//for api:
//User messages
//          /user/%id/messages
//          /user/%id/messages/%id
//Groups messages
//          /groups/%id/messages
//          /groups/%id/messages/%id

import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.pocket.messenger.client.model.ServerResponse;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.MessageFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.MessageToServer;
import ru.geekbrains.pocket.messenger.client.utils.Connector;
import ru.geekbrains.pocket.messenger.client.utils.Converter;
import ru.geekbrains.pocket.messenger.client.utils.HTTPSRequest;
import ru.geekbrains.pocket.messenger.client.utils.Sound;
import ru.geekbrains.pocket.messenger.client.view.ChatViewController;
import ru.geekbrains.pocket.messenger.database.dao.DataBaseService;
import ru.geekbrains.pocket.messenger.database.entity.Message;
import ru.geekbrains.pocket.messenger.database.entity.User;

import java.io.IOException;
import java.sql.Timestamp;

import static ru.geekbrains.pocket.messenger.client.utils.Common.showAlert;

public class MessageController {
    private static final Logger log = LogManager.getLogger(MessageController.class);
    private static MessageController instance;
    private ChatViewController chatViewController;
    private Connector connector;
    private ContactController contactController;
    private DataBaseService dbService;

    public MessageController(){
        chatViewController = ChatViewController.getInstance();
        connector = Connector.getInstance();
        contactController = ContactController.getInstance();
        dbService = DataBaseService.getInstance();
    }

    public static MessageController getInstance() {
        if (instance == null) {
            instance = new MessageController();
        }
        return instance;
    }

    public void receiveMessage(User receiver, String message, String token) {
        MessageFromServer mfs = MessageFromServer.fromJson(message);
        if (!contactController.getContactList().contains(mfs.getSenderId())) { //Проверяем, что осообщение пришло не от клиента в списке
            try {
                ServerResponse response = HTTPSRequest.getUser(mfs.getSenderId(), null, token);
                switch (response.getResponseCode()) {
                    case 200:
                        contactController.addContact(Converter.convertUserProfileJSONToUser(response.getResponseJson()));
                        break;
                    case 404:
                        showAlert("Пользователь не найден", Alert.AlertType.ERROR);//с id: " + mfs.getSenderid() + "
                        break;
                    default:
                        showAlert("Общая ошибка!", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                log.error("HTTPSRequest.getUser_error", e);
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

    public void sendMessage(User myUser, User receiver, String message) {
        if (receiver == null) {
            showAlert("Выберите контакт для отправки сообщения", Alert.AlertType.ERROR);
            return;
        }

        String jsonMessage =
                new MessageToServer(message, null, receiver.getId().toString(), null).toJson();
        try {
            connector.getChatClient().send(jsonMessage);
            //todo допилить получение Success/Error и MessageId из ответа

//            dbService.addMessage(receiver.getId(),
//                    user.getId(),
//                    new Message(message, new Timestamp(System.currentTimeMillis()))
//            );
            chatViewController.showMessage(myUser.getAccount_name(), message, new Timestamp(System.currentTimeMillis()), false);

        } catch (IOException ex) {
            showAlert("Потеряно соединение с сервером", Alert.AlertType.ERROR);
            log.error(ex);
        }
    }
}
