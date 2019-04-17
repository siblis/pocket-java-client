package ru.geekbrains.pocket.messenger.client.utils;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.geekbrains.pocket.messenger.client.controller.ClientController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.pocket.messenger.client.controller.MessageController;

import java.util.ArrayList;
import java.util.List;

public class Connector {
    private static final Logger connectorLogger = LogManager.getLogger(Connector.class.getName());
    //резервный сервер при простое (30 мин) засыпает и просыпается пару минут
//    static final String connectTo = "pocket-java-backend.herokuapp.com"; //rezerv
    static final String connectTo = "java-api.pocketmsg.ru"; //java backend on product server
//    static final String connectTo = "api.pocketmsg.ru"; //node backend on product server

    private WebSocketStompClient stompClient;

    public Connector(String token, ClientController clientCtrllr){
        try {
            connect(token, clientCtrllr);
        } catch (Exception e) {
            connectorLogger.error("Connector_error", e);
        }
    }


    public void connect(String token, ClientController clientCtrllr) {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        SockJsClient sockJsClient = new SockJsClient(transports);

        String url = "wss://" + connectTo + "/socket?token=" + token;
        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSessionHandler sessionHandler = new ChatStompSessionHandler(clientCtrllr);
        stompClient.connect(url, sessionHandler);
    }

    public void disconnect() {
        stompClient.stop();
        stompClient = null;
    }
}
