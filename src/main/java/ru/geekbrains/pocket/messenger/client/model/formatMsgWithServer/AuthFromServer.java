package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.geekbrains.pocket.messenger.database.entity.User;

public class AuthFromServer {

    public static AuthFromServer fromJson(String jsonAnswer) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonAnswer, AuthFromServer.class);
    }

    public String token;
    public User user;
}
