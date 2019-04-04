package ru.geekbrains.pocket.messenger.client.controller;

//for api:  /auth/login/
//          /auth/registration/

import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.AuthFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.AuthToServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.RegistrationFromServer;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.RegistrationToServer;
import ru.geekbrains.pocket.messenger.client.model.pub.UserPub;
import ru.geekbrains.pocket.messenger.client.utils.Connector;
import ru.geekbrains.pocket.messenger.client.utils.HTTPSRequest;
import ru.geekbrains.pocket.messenger.database.dao.DataBaseService;
import ru.geekbrains.pocket.messenger.database.entity.User;

public class AuthController {
    private static final Logger log = LogManager.getLogger(AuthController.class);

    private static AuthController instance;
    private DataBaseService dbService;
    private ClientController clientController;
    private Connector connector;
    private ContactController contactController;

    public AuthController(){
        clientController = ClientController.getInstance();
        connector = Connector.getInstance();
        contactController = ContactController.getInstance();
    }

    public static AuthController getInstance() {
        if (instance == null) {
            instance = new AuthController();
        }
        return instance;
    }

    public boolean proceedRegister(String name, String password, String email) {
//        String requestJSON = "{" +
//                "\"email\": \"" + email + "\"," +
//                "\"password\": \"" + password + "\"," +
//                "\"name\": \"" + name + "\"" +
//                "}";
        RegistrationToServer registrationToServer = new RegistrationToServer(email, password, name);
        try {
            RegistrationFromServer registrationFromServer = HTTPSRequest.registration(registrationToServer);
            if (registrationFromServer != null) {
                UserPub userPub = registrationFromServer.getUser();
                clientController.setToken(registrationFromServer.getToken());
                if (userPub != null && clientController.getToken() != null) {
                    User user = new User(userPub);
                    clientController.setMyUser(user);
                    dbService.setUserDB(user);
                    dbService.insertUser(user);
                    dbService.close();

                    return true;
                }
            }
        } catch (Exception e) {
            log.error("HTTPSRequest.registration", e);
        }
        return false;
    }

    private boolean authentication(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()) {
            String answer = "0";
            try {
                answer = HTTPSRequest.authorization(new AuthToServer(email, password).toJson());
            } catch (Exception e) {
                log.error("HTTPSRequest.authorization_error", e);
            }
            if (answer.contains("token")) {
                AuthFromServer authFromServer = AuthFromServer.fromJson(answer);
                clientController.setToken(authFromServer.getToken());
                UserPub userPub = authFromServer.getUser();
                if (userPub != null) {
                    System.out.println("answer server " + clientController.getToken() + "\n" + userPub);
                    dbService.setUserDB(userPub.getProfile().getUsername());
                    User user = dbService.getUserByEmail(userPub.getEmail());
                    if (user == null) {
                        user = new User(userPub);
                        dbService.insertUser(user);
                    } else {
                        user = user.update(userPub);
                        dbService.updateUser(user);
                    }
                    clientController.setMyUser(user);
                    try {
                        connector.connect();
                    } catch (Exception e) {
                        log.error("Connector_error", e);
                    }                    //connect(token);
                    contactController.synchronizeContactList(user, clientController.getToken());

                    return true;
                }
            } else {
//                showAlert("Ошибка авторизации!", Alert.AlertType.ERROR);
                log.info("Ошибка авторизации!", Alert.AlertType.ERROR);
            }
        } else {
//            showAlert("Неполные данные для авторизации!", Alert.AlertType.ERROR);
            log.info("Неполные данные для авторизации!", Alert.AlertType.ERROR);
            return false;
        }
        return false;
    }

    public boolean proceedLogIn(String login, String password) {
        return authentication(login, password);
    }

    public String proceedRestorePassword(String email) {
        String answer = "0";
        String requestJSON = "{" +
                "\"email\": \"" + email + "\"" +
                "}";
        try {
            answer = HTTPSRequest.restorePassword(requestJSON);
        } catch (Exception e) {
            log.error("proceedRestorePassword_error", e);
        }
        return answer;
    }

    public String proceedChangePassword(String email, String codeRecovery, String password) {
        String answer = "0";
        String requestJSON = "{" +
                "\"email\": \"" + email + "\"," +
                "\"code\": \"" + codeRecovery + "\"," +
                "\"password\": \"" + password + "\"" +
                "}";
        try {
            answer = HTTPSRequest.changePassword(requestJSON);
        } catch (Exception e) {
            log.error("proceedChangePassword_error", e);
        }
        return answer;
    }
}
