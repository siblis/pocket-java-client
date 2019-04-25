package ru.geekbrains.pocket.messenger.client.controller;

import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.AuthFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.AuthToServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.RegistrationToServer;
import ru.geekbrains.pocket.messenger.client.utils.Connector;
import ru.geekbrains.pocket.messenger.client.utils.Converter;
import ru.geekbrains.pocket.messenger.client.utils.HTTPSRequest;
import ru.geekbrains.pocket.messenger.database.entity.User;

import static ru.geekbrains.pocket.messenger.client.controller.ClientController.token;
import static ru.geekbrains.pocket.messenger.client.utils.Common.showAlert;

public class AuthController {

    static final Logger controllerLogger = LogManager.getLogger(AuthController.class);
    
    ClientController cc;

    AuthController(ClientController cc) {
        this.cc = cc;
    }

    boolean registration(String name, String password, String email) {
        AuthFromServer responseFromServer = sendRegRequest(name, password, email);
        if (responseFromServer != null) {
            if (responseFromServer.getUser() != null) {
                User regUser = responseFromServer.getUser().toUser();
                cc.dbService.setUserDB(regUser);
                cc.dbService.insertUser(regUser);
                return true;
            }
        }
        return false;
    }

    private AuthFromServer sendRegRequest(String name, String password, String email) {
        String jsonRegData = Converter.toJson(new RegistrationToServer(email, password, name));
        int responseCode = 0;
        AuthFromServer responseFromServer = null;
        try {
            responseCode = HTTPSRequest.sendRequest("/auth/registration", "POST", jsonRegData, null);
            responseFromServer = HTTPSRequest.getResponse(AuthFromServer.class);
        } catch (Exception e) {
            controllerLogger.error("HTTPSRequest.registration_error", e);
        }
        switch (responseCode) {
            case 201:
                return responseFromServer;
            case 429:
                showAlert("Отправлено слишком много запросов...", Alert.AlertType.WARNING);
                break;
            case 409:
                showAlert("Указанный E-mail уже используется", Alert.AlertType.INFORMATION);
                break;
            case 400:
                //todo: извлечение информации из ValidationError
                showAlert("Ошибка регистрации, код: " + responseCode, Alert.AlertType.ERROR);
        }
        return null;
    }

    boolean login(String login, String password) {
        AuthFromServer authFromServer = sendAuthRequest(login, password);
        if (authFromServer != null && authFromServer.getUser() != null) {
            token = authFromServer.getToken();
            User authUser = authFromServer.getUser().toUser();
            cc.dbService.setUserDB(authUser);
            if (!cc.hasUserInLocalDB(authUser.getId())) {
                cc.dbService.insertUser(authUser);
            } else {
                cc.dbService.updateUser(authUser);
            }
            cc.myUser = authUser;
            cc.conn = new Connector(token, cc);
            cc.contactService.synchronizeContactList();
            return true;
        }
        return false;
    }

    private AuthFromServer sendAuthRequest(String login, String password) {
        if (!login.isEmpty() && !password.isEmpty()) {
            String jsonLoginData = Converter.toJson(new AuthToServer(login, password));
            int responseCode = 0;
            AuthFromServer authFromServer = null;
            try {
                responseCode = HTTPSRequest.sendRequest("/auth/login", "POST", jsonLoginData, null);
                authFromServer = HTTPSRequest.getResponse(AuthFromServer.class);
            } catch (Exception e) {
                controllerLogger.error("HTTPSRequest.authorization_error", e);
            }
            switch (responseCode) {
                case 200:
                    return authFromServer;
                case 429:
                    showAlert("Слишком много запросов!", Alert.AlertType.WARNING);
                    break;
                case 404:
                    showAlert("Неверные учётные данные!", Alert.AlertType.ERROR);
                    break;
            }
        } else {
            showAlert("Неполные данные для авторизации!", Alert.AlertType.ERROR);
            controllerLogger.error("Неполные данные для авторизации!");
        }
        return null;
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
