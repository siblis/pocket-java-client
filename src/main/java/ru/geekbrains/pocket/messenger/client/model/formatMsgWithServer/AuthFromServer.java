package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.messenger.client.model.pub.UserPub;
import ru.geekbrains.pocket.messenger.database.entity.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthFromServer {

    public String token;
    public UserPub user;

    public static AuthFromServer fromJson(String jsonAnswer) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonAnswer, AuthFromServer.class);
    }

}
