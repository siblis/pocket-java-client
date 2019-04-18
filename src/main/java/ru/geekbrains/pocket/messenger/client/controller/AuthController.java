package ru.geekbrains.pocket.messenger.client.controller;

import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.AuthFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.AuthToServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.RegistrationToServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.UserFromServer;
import ru.geekbrains.pocket.messenger.client.utils.Connector;
import ru.geekbrains.pocket.messenger.client.utils.Converter;
import ru.geekbrains.pocket.messenger.client.utils.HTTPSRequest;
import ru.geekbrains.pocket.messenger.database.entity.User;

import static ru.geekbrains.pocket.messenger.client.controller.ClientController.token;
import static ru.geekbrains.pocket.messenger.client.utils.Common.showAlert;

public class AuthController {

    static final Logger controllerLogger = LogManager.getLogger(AuthController.class);
    
    ClientController clientCtrllr;

    AuthController(ClientController cc) {
        clientCtrllr = cc;
    }

    boolean registration(String name, String password, String email) {
        String jsonRegData = Converter.toJson(new RegistrationToServer(email, password, name));
        try {
            int regStatus = HTTPSRequest.sendRequest("/auth/registration", "POST", jsonRegData);
            AuthFromServer authFromServer = HTTPSRequest.getResponse(AuthFromServer.class);
            if (authFromServer != null) {
                switch (regStatus) {
                    case 201:
                        UserFromServer registered = authFromServer.getUser();
                        token = authFromServer.getToken();
                        if (registered != null && token != null) {
                            clientCtrllr.myUser = registered.toUser();
                            clientCtrllr.dbService.setUserDB(clientCtrllr.myUser);
                            clientCtrllr.dbService.insertUser(clientCtrllr.myUser);
                            return true;
                        }
                    case 429: 
                        showAlert("Отправлено слишком много запросов...", Alert.AlertType.WARNING);
                        break;
                    case 409:
                        showAlert("Указанный E-mail уже используется", Alert.AlertType.INFORMATION);
                        break;
                    case 400:
                        //todo: извлечение информации из ValidationError
                        showAlert("Ошибка регистрации, код: " + regStatus, Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.registration", e);
        }
        return false;
    }

    boolean login(String login, String password) {
        if (!login.isEmpty() && !password.isEmpty()) {
            String jsonLoginData = Converter.toJson(new AuthToServer(login, password));
            int regStatus = 0;
            AuthFromServer authFromServer = null;
            try {
                regStatus = HTTPSRequest.sendRequest("/auth/login", "POST", jsonLoginData);
                authFromServer = HTTPSRequest.getResponse(AuthFromServer.class);
            } catch (Exception e) {
                controllerLogger.error("HTTPSRequest.authorization_error", e);
            }
            switch (regStatus) {
                case 200:
                    if (authFromServer != null && authFromServer.getUser() != null) {
                        token = authFromServer.getToken();
                        User myUser = authFromServer.getUser().toUser();
                        System.out.println("answer server " + token + "\n" + myUser);
                        clientCtrllr.dbService.setUserDB(myUser);
                        clientCtrllr.myUser = clientCtrllr.dbService.getUserById(myUser.getId());
                        if (clientCtrllr.myUser == null) {
                            clientCtrllr.dbService.insertUser(myUser);
                        } else {
                            clientCtrllr.dbService.updateUser(myUser);
                        }
                        clientCtrllr.myUser = myUser;
                        clientCtrllr.conn = new Connector(token, clientCtrllr);
                        clientCtrllr.contactService.synchronizeContactList();

                        return true;
                    }
                    break;
                case 429:
                    showAlert("Слишком много запросов!", Alert.AlertType.WARNING);
                    break;
                case 404:
                    showAlert("Неверные учётные данные!", Alert.AlertType.ERROR);
                    break;
                default:
                    showAlert("Ошибка авторизации!", Alert.AlertType.ERROR);
                    controllerLogger.error("Ошибка авторизации!");
            }
        } else {
            showAlert("Неполные данные для авторизации!", Alert.AlertType.ERROR);
            controllerLogger.error("Неполные данные для авторизации!");
        }
        return false;
    }

    String changePassword(String email, String codeRecovery, String password) {
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

    String restorePassword(String email) {
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
}
