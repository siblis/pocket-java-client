package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private static final long TIMEOUT = 120_000;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;

    public String getNick() {
        return nick;
    }

    public ClientHandler(Server server, Socket socket) {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/auth")) {
                            String[] authData = msg.split("\\s");

                            String newNick = null;

                            // fix bug (отправка пустых логина и пароля приводит к выходу за границы массива)
                            if (authData.length == 3)
                                newNick = server.getAuthService().getNickByLoginAndPass(authData[1], authData[2]);
                            if (newNick != null) {
                                if (server.isNickBusy(newNick)) {
                                    sendMessage("данный пользователь уже авторизован");
                                    continue;
                                }
                                nick = newNick;
                                sendMessage("/authok " + nick);
                                server.subscribe(this);
                                break;
                            } else {
                                sendMessage("Неверный логин/пароль");
                            }
                        }
                    }
                    while (true) {
                        String msg = in.readUTF();
                        System.out.println(nick + ": " + msg);

                        if (msg.startsWith("/")) {
                            if (msg.equals("/end")) break;
                            if (msg.startsWith("/w")) {
                                String[] data = msg.split("\\s", 3);
                                server.privateSender(this, data[1], data[2]);
                            }
                            continue;
                        }
                        server.broadcastSender(nick + ": " + msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            new Thread(() -> {
                try {
                    Thread.sleep(TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(nick == null){
                    try {
                        sendMessage("/timeout");
                        socket.close();
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("timeout");
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
