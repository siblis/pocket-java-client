package ru.geekbrains.pocket.messenger.client.utils;

import ru.geekbrains.pocket.messenger.client.controller.ClientController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.URI;

public class Connector {
    private static final Logger log = LogManager.getLogger(Connector.class.getName());
    static final String connectTo = "pocket-java-backend.herokuapp.com";

    private static Connector instance;
    private ClientController clientController;
    private WebSocketChatClient chatClient;


    public Connector(){
        clientController = ClientController.getInstance();
//        try {
//            connect(clientController.getToken(),clientController);
//        } catch (Exception e) {
//            log.error("Connector_error", e);
//        }
    }

//    public Connector(String token, ClientController controller){
//        try {
//            connect(token,controller);
//        } catch (Exception e) {
//            log.error("Connector_error", e);
//        }
//    }

    public static Connector getInstance() {
        if (instance == null) {
            instance = new Connector();
        }
        return instance;
    }

    public WebSocketChatClient getChatClient() throws IOException {
        if (chatClient == null) {
            throw new IOException("Connection with socket is closed");
        }
        return chatClient;
    }

    public void connect() throws Exception {
        chatClient = new WebSocketChatClient(
                new URI("wss://" + connectTo + "/v1/socket/token:" + clientController.getToken()),
                null, clientController);
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        chatClient.setSocketFactory(factory);
        chatClient.connectBlocking();
    }

//    public void connect(String token, ClientController controller) throws Exception {
//        chatClient = new WebSocketChatClient(
//                new URI("wss://" + connectTo + "/v1/socket/token:" + token), null, controller);
//        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
//        chatClient.setSocketFactory(factory);
//        chatClient.connectBlocking();
//    }

    public void disconnect() {
        chatClient.close();
        chatClient = null;
        clientController = null;
        instance = null;
    }
}
