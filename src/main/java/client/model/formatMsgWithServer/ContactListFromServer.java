package client.model.formatMsgWithServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ContactListFromServer {

    public static ContactListFromServer fromJson(String jsonAnswer) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonAnswer, ContactListFromServer.class);
    }

    private String user; //id пользователя которому принадлежит коллекция!?
    private int offset;
    private ContactFromServer[] data;

    public ContactListFromServer() {}

    public String getUserId() {
        return user;
    }

    public int getOffset() {
        return offset;
    }

    public ContactFromServer[] getContacts() {
        return data;
    }
}