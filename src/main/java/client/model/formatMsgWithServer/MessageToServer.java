package client.model.formatMsgWithServer;

public class MessageToServer {
    private long receiver;
    private String message;

    public MessageToServer(){}

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
