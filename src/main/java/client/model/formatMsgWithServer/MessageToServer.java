package client.model.formatMsgWithServer;

public class MessageToServer {
    private long receiver;
    private String text;

    public MessageToServer(){}

    public MessageToServer(long receiver, String message) {
        this.receiver = receiver;
        this.text = message;
    }

    public long getReceiver() {
        return receiver;
    }

    public void setReceiver(long receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return text;
    }

    public void setMessage(String message) {
        this.text = message;
    }
}
