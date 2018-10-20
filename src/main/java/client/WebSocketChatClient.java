package client;



import client.controller.TestEnterViewController;
import javafx.application.Platform;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.SocketFactory;
import java.net.URI;
import java.nio.channels.NotYetConnectedException;
import java.util.Map;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WebSocketChatClient extends WebSocketClient {
    private SocketFactory socketFactory = null;
    private TestEnterViewController controller = null;

//    public WebSocketChatClient(URI serverUri, Map<String, String> httpHeaders, TestEnterViewController controller) {
//        super( serverUri );
//        controller = controller;
//    }

    public WebSocketChatClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }


    public WebSocketChatClient(URI serverUri, Map<String, String> httpHeaders, TestEnterViewController conn) {
        super(serverUri, httpHeaders);
        controller = conn;
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        System.out.println( "Connected" );

    }

    @Override
    public void onMessage( String message ) {
        System.out.println( "got: " + message );
//        if(!message.startsWith("{")){
         if (message.contains("receiver")){
             Platform.runLater(() -> controller.convertMFStoMessage(message));
//            Platform.runLater(() -> controller.reciveMessage(message));
        }
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        System.out.println( "Disconnected" );
        System.exit( 0 );

    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();

    }
    void setSocketFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

}


