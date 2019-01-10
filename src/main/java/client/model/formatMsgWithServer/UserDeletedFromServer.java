package client.model.formatMsgWithServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import database.entity.User;

public class UserDeletedFromServer {
    private long deleted_contact_id;
    private String deleted_contact_username;
    private String deleted_contact_email;// на 08.01.2018 нет в ответе сервера, надо будет?

    public UserDeletedFromServer(String responseJson) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        UserDeletedFromServer temp = gson.fromJson(responseJson, getClass());
        deleted_contact_id = temp.deleted_contact_id;
        deleted_contact_username = temp.deleted_contact_username;
        deleted_contact_email = temp.deleted_contact_email;
    }
    
    public User toUser() {
        return new User(deleted_contact_id, deleted_contact_username, deleted_contact_email);
    }
}
