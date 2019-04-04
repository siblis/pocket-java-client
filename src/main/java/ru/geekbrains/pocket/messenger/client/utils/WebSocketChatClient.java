package ru.geekbrains.pocket.messenger.client.utils;

import ru.geekbrains.pocket.messenger.client.controller.ClientController;
import javafx.application.Platform;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import ru.geekbrains.pocket.messenger.client.controller.MessageController;

import javax.net.SocketFactory;
import java.net.URI;
import java.util.Map;

public class WebSocketChatClient extends WebSocketClient {
    private SocketFactory socketFactory = null;
    private ClientController clientController = null;
    private MessageController messageController;

//    public WebSocketChatClient(URI serverUri, Map<String, String> httpHeaders, TestEnterViewController controller) {
//        super( serverUri );
//        controller = controller;
//    }

    public WebSocketChatClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }


    public WebSocketChatClient(URI serverUri, Map<String, String> httpHeaders, ClientController clientController) {
        super(serverUri, httpHeaders);
        this.clientController = clientController;
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        System.out.println( "Connected" );

    }

    @Override
    public void onMessage( String message ) {
        System.out.println( "got: " + message );
        //todo рубим sendMessage кавычками
        if (message.contains("\"receiver\"")){
            Platform.runLater(() -> messageController.receiveMessage(clientController.getMyUser(), message, clientController.getToken()));
        }
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        System.out.println( "Disconnected" );
//        System.exit( 0 ); завершать JVM для закрытия сокета - не лучший выход :)

    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();

    }
    void setSocketFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

}



