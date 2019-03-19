package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.geekbrains.pocket.messenger.database.entity.User;
import ru.geekbrains.pocket.messenger.database.entity.UserProfile;

import java.sql.Timestamp;

public class ContactFromServer {

    public static ContactFromServer fromJson(String jsonAnswer) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonAnswer, ContactFromServer.class);
    }

    private UserProfile contact;
    private String byname;
    private Timestamp added_at;

    public ContactFromServer() {}

    public UserProfile getUserProfile() {
        return contact;
    }

    public String getByname() {
        return byname;
    }

    public Timestamp getAdded_at() {
        return added_at;
    }

    public User toUser() {
        return new User(null, contact);
    }
}