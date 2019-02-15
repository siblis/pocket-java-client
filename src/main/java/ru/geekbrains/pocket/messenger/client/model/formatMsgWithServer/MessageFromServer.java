package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Timestamp;

public class MessageFromServer {

    public static MessageFromServer fromJson(String jsonAnswer) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonAnswer, MessageFromServer.class);
    }

    private String id;
    private String type;
    private String sender;
    private String recipient;
    private String group;
    private String text;
    private boolean read;
    private Timestamp sent_at;

    public MessageFromServer() {
    }

    public String getMessageId() {
        return id;
    }

    public String getMessageType() {
        return type;
    }

    public String getSenderId() {
        return sender;
    }

    public String getReceiverId() {
        return recipient;
    }

    public String getGroup() {
        return group;
    }

    public String getMessage() {
        return text;
    }

    public boolean isRead() {
        return read;
    }

    public Timestamp getTimestamp() {
        return sent_at;
    }
}
