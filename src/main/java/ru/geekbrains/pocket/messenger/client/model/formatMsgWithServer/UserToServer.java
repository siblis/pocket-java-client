package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import com.google.gson.Gson;

public class UserToServer {
    private String user;
    private String byname;

    public UserToServer(String userId, String byname) {
        this.user = userId;
        this.byname = byname;
    }

    public String toJson() {
        return new Gson().toJson(this);
//        return "{" +
//                "\"user\": \"" + user + "\"," +
//                "\"" + byname + "\"" +
//                "}";
    }
}
