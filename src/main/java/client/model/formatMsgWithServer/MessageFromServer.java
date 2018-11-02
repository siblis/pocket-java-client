package client.model.formatMsgWithServer;

import java.sql.Timestamp;

public class MessageFromServer {
    private long receiver;
    private String message;
    private long senderid;
    private String sender_name;
    private String timestamp;

    public MessageFromServer() {
    }

    public long getReceiver() {
        return receiver;
    }

    public void setReceiver(long receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSenderid() {
        return senderid;
    }

    public void setSenderid(long senderid) {
        this.senderid = senderid;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public Timestamp getTimestamp() {
        long longTS = Long.parseLong(timestamp.substring(0, 10) + timestamp.substring(10 + 1));
        return new Timestamp(longTS / 1000);
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
