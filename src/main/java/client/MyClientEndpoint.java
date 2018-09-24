package client;

import javax.websocket.*;

@ClientEndpoint
public class MyClientEndpoint {
    Controller controller;

    public MyClientEndpoint(Controller controller) {
        this.controller = controller;
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to endpoint: " + session.getBasicRemote());
    }

    @OnMessage
    public void processMessage(String message) {
        System.out.println("Received message in client: " + message);
        controller.reciveMessage(message);
    }

    @OnError
    public void processError(Throwable t) {
        t.printStackTrace();
    }
}
