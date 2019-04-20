package ru.geekbrains.pocket.messenger.client.utils;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import ru.geekbrains.pocket.messenger.client.controller.ClientController;
import ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer.MessageFromServer;

import java.lang.reflect.Type;

public class ChatStompSessionHandler extends StompSessionHandlerAdapter {
    private static final Logger sessionLogger = LogManager.getLogger(ChatStompSessionHandler.class.getName());
    private ClientController clientCtrllr;

    public ChatStompSessionHandler(ClientController clientCtrllr) {
        this.clientCtrllr = clientCtrllr;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        clientCtrllr.setMessageSession(session);
        sessionLogger.info("websocket connection established");

        session.subscribe("/user/queue/new", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return MessageFromServer.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object mfs) {
                Platform.runLater(() -> clientCtrllr.receiveMessage((MessageFromServer) mfs));
            }
        });

        session.subscribe("/user/queue/send", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object o) {
                String s = (String) o;
                if (s.startsWith("Error")) {
                    sessionLogger.debug(s);
                    clientCtrllr.resetWaitForConfirm();
                } else {
                    Platform.runLater(() -> clientCtrllr.saveToDBAndShowMessage(s));
                }
            }
        });
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        sessionLogger.debug("Handling exception", exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        sessionLogger.error("Transport error", exception);
    }
}
