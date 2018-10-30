package client.model.formatMsgWithServer;

public class MessageToServer {
    private long receiver;
    private String message;

    public MessageToServer(){}

    public MessageToServer(long receiver, String message) {
        this.receiver = receiver;
        this.message = message;
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
}
