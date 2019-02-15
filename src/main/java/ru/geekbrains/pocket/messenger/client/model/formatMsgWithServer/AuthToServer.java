package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import com.google.gson.Gson;

public class AuthToServer {
    public String email;
    public String password;

    public AuthToServer() {

    }

    public AuthToServer(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    public String toJson() {
        return new Gson().toJson(this);
    }
}
