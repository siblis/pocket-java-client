package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server {
    private Vector<ClientHandler> clientList;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            clientList = new Vector<>();

            authService = new AuthService();
            authService.connect();

            System.out.println("Server started...Waiting for clients");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected" +
                        socket.getInetAddress() + ":" +
                        socket.getPort() + ":" +
                        socket.getLocalPort());
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            System.err.println("Запрашиваемый порт занят");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Не удалось запустить сервис авторизации");
        } finally {
            authService.disconnect();
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clientList.add(clientHandler);
        broadcastClientListSender();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clientList.remove(clientHandler);
        broadcastClientListSender();
    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler ch : clientList)
            if (ch.getNick().equals(nick)) return true;

        return false;
    }

    public void broadcastSender(String message) {
        for (ClientHandler ch :
                clientList) {
            ch.sendMessage(message);
        }
    }

    public void privateSender(ClientHandler sender, String destNnick, String message) {
        for (ClientHandler ch :
                clientList) {
            if (destNnick.equals(ch.getNick())) {
                ch.sendMessage(sender.getNick() + " private: " + message);
                sender.sendMessage("private to " + destNnick + ": " + message);
                return;
            }
        }
        sender.sendMessage("клиент с ником " + destNnick + " не найден");
    }

    private void broadcastClientListSender() {
        StringBuilder sb = new StringBuilder("/clients ");
        for (ClientHandler ch :
                clientList) {
            sb.append(ch.getNick()).append(" ");
        }
        String clientsListMsg = sb.toString();
        for (ClientHandler ch :
                clientList) {
            ch.sendMessage(clientsListMsg);
        }
    }
}
