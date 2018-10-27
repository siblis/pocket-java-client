package client.model.formatMsgWithServer;

public class MessageToServer {
    private String receiver;
    private String message;

    public MessageToServer(){

    }
    public MessageToServer(String receiver, String message){
        this.receiver = receiver;
        this.message = message;
    }
}
