package client;

import server.SocketThread;

import java.net.Socket;

public interface SocketThreadListener {
    void onStartSocketThread(SocketThread socketThread, Socket socket);

    void onStopSocketThread(SocketThread thread, Socket socket);

    void onSocketIsReady(SocketThread socketThread, Socket socket, Object ChatServer);

    void onSocketIsReady(SocketThread thread, Socket socket);

    void onReceivedString(SocketThread socketThread, Socket socket, String msg);

}