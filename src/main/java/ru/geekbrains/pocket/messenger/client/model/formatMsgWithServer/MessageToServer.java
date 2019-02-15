package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import com.google.gson.Gson;

public class MessageToServer {
    private String text;
    private String group;
    private String recipient;
    private String messageId;

    public MessageToServer(){}

    /**
     * Для отправки сообщения, либо получателю ({@code recipient != null} & 
     * {@code group == null}), либо в группу ({@code recipient == null} & 
     * {@code group != null}).
     *
     * @param text текст сообщения
     * @param groupId id группы (для сообщения в группу)
     * @param recipientId id получателя (для личного сообщения)
     * @param messageId временное id сообщения!?
     */
    public MessageToServer(String text, String groupId, String recipientId, String messageId) {
        this.text = text;
        this.group = groupId;
        this.recipient = recipientId;
        this.messageId = messageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}