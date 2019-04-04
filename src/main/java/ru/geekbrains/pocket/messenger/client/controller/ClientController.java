package ru.geekbrains.pocket.messenger.client.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.pocket.messenger.client.model.pub.UserProfilePub;
import ru.geekbrains.pocket.messenger.client.utils.Connector;
import ru.geekbrains.pocket.messenger.client.view.ChatViewController;
import ru.geekbrains.pocket.messenger.client.view.customFX.CFXListElement;
import ru.geekbrains.pocket.messenger.database.dao.DataBaseService;
import ru.geekbrains.pocket.messenger.database.entity.User;
import ru.geekbrains.pocket.messenger.database.entity.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class ClientController {
    private static final Logger log = LogManager.getLogger(ClientController.class);
    private static ClientController instance;
//    private DataBaseService dbService;
    private String token;

    private User myUser = null;
//    private Connector conn;

    private ClientController() {
//        receiver = null;
//        myUser = null;
//        conn = null;
//        dbService = new DataBaseService();
    }

    public static ClientController getInstance() {
        if (instance == null) {
            instance = new ClientController();
        }
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getMyUser() {
        return myUser;
    }

    public void setMyUser(User myUser) {
        this.myUser = myUser;
    }

//    private void connect(String token) {
//        conn = new Connector(token, getInstance());
//    }

    public String getSenderName() {
        return myUser.getAccount_name();
    }

    public void disconnect() {
//        if (conn != null) {
//            conn.disconnect();
//            conn = null;
//        }
//        if (dbService != null) {
//            dbService.close();
//            dbService = null;
//        }
        instance = null;
//        contactList = null;
//        contactListOfCards = null;
    }

//    public List<String> getAllUserNames() {
//        return dbService.getAllUserNames();
//    }

}
