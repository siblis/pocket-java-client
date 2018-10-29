package client.model.formatMsgWithServer;

public class MessageToServer {
    public String receiver;
    public String message;

    public MessageToServer(){

    }
    public MessageToServer(String receiver, String message){
        this.receiver = receiver;
        this.message = message;
    }
}
