package ru.geekbrains.pocket.messenger.client.controller;

import ru.geekbrains.pocket.messenger.database.entity.User;

public class ClientController {
    private static ClientController instance;
    private String token;
    private User myUser = null;

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

    public String getSenderName() {
        return myUser.getAccount_name();
    }

    public void disconnect() {
        instance = null;
    }

}
