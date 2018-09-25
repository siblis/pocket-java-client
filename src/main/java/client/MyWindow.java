package client;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MyWindow extends JFrame {

    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 8189;

    private JTextField login = new JTextField("Login");
    private JTextField password = new JTextField("Password");
    private JButton authBtn = new JButton("Auth");
    private JTextField jtf;
    private JTextArea jta;

    private Socket sock;
    private DataInputStream in;
    private DataOutputStream out;

    public static void main(String[] args) {
        new MyWindow().setVisible(true);
    }

    public MyWindow() {
        initUI();
    }

    private void initUI() {
        setBounds(600, 300, 500, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        createAuthPanel();

        jta = new JTextArea();
        jta.setEditable(false);
        jta.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(jta);
        add(jsp, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        JButton jbSend = new JButton("SEND");
        bottomPanel.add(jbSend, BorderLayout.EAST);
        jtf = new JTextField();
        jtf.setEnabled(false);
        bottomPanel.add(jtf, BorderLayout.CENTER);

        jbSend.addActionListener(e -> sendMsgFromUI());
        jtf.addActionListener(e -> sendMsgFromUI());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                super.windowClosing(event);
                try {
                    out.writeUTF("end");
                    sock.close();
                    out.close();
                    in.close();
                } catch (IOException e) {
                    System.out.println("something happend on closing");
                }
            }
        });
    }

    private void createAuthPanel() {
        JPanel authPanel = new JPanel(new GridLayout());
        authPanel.add(login);
        login.setToolTipText("enter your login");
        password.setToolTipText("enter your password");
        authPanel.add(password);
        authPanel.add(authBtn);
        add(authPanel, BorderLayout.NORTH);
        authBtn.addActionListener(e -> connect(login.getText(), password.getText()));
    }

    private void connect(String login, String password) {

        if (login.trim().isEmpty() || password.trim().isEmpty()) {
            System.out.println("login or password is empty!");
            return;
        }

        jtf.setEnabled(true);
        try {
            sock = new Socket(SERVER_ADDR, SERVER_PORT);
            in = new DataInputStream(sock.getInputStream());
            out = new DataOutputStream(sock.getOutputStream());
            out.writeUTF("/auth " + login + " " + password);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String msg = in.readUTF();
                        if (msg != null) {
                            if (msg.equalsIgnoreCase("end session")) break;
                            jta.append(msg + System.lineSeparator());
                        }
                    }
                    Thread.sleep(100);
                } catch (Exception e) {
                }
            }
        }).start();
    }

    private void sendMsgFromUI() {
        String msg = jtf.getText();
        sendMsg(msg);
        jtf.setText("");
        jtf.grabFocus();
    }

    private void sendMsg(String msg) {
        if (!msg.trim().isEmpty()) {
            try {
                out.writeUTF(msg);
                out.flush();
            } catch (IOException e) {
                System.out.println("Fail to send message: " + e.getLocalizedMessage());
            }
        }
    }
}