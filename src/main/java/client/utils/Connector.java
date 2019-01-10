package client.utils;

import client.controller.ClientController;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLSocketFactory;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Connector {
    private static final Logger connectorLogger = LogManager.getLogger(Connector.class.getName());
    private WebSocketChatClient chatClient;

    public Connector(String token, ClientController controller){
        try {
            connect(token,controller);
        } catch (Exception e) {
            connectorLogger.error("Connector_error", e);
        }
    }

    public WebSocketChatClient getChatClient() throws IOException {
        if (chatClient == null) {
            throw new IOException("Connection with socket is closed");
        }
        return chatClient;
    }

    public void connect(String token, ClientController controller) throws Exception {
        Map<String,String> httpHeaders = new HashMap<String, String>();
        httpHeaders.put("Token",token);

        chatClient = new WebSocketChatClient(
                new URI("wss://pocketmsg.ru:8888/v1/ws/"),httpHeaders,controller);
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        chatClient.setSocketFactory(factory);
        chatClient.connectBlocking();
    }

    public void disconnect() {
        chatClient.close();
        chatClient = null;
    }
}
