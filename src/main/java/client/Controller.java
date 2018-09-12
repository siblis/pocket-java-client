package client;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField msgField;
    @FXML
    private HBox loginPanel;
    @FXML
    private HBox messagePanel;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passFiead;
    @FXML
    private ListView clientsListArea;

    @FXML
    private VBox regPanel;
    @FXML
    private Button okButtonReg;
    @FXML
    private TextField regLoginField;
    @FXML
    private TextField regPassField;
    @FXML
    private TextField regPassField2;
    @FXML
    private TextField regEmailField;

    private ObservableList<String> clientsObsvList;

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String myNick;

    final String SERVER_IP = "localhost";
    final int SERVER_PORT = 8189;

    public void setAutorized(boolean autorized) {
        if (autorized) {
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            messagePanel.setVisible(true);
            messagePanel.setManaged(true);
        } else {
            loginPanel.setVisible(true);
            loginPanel.setManaged(true);
            messagePanel.setVisible(false);
            messagePanel.setManaged(false);
            myNick = "";
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAutorized(false);
        clientsObsvList = FXCollections.observableArrayList();
        clientsListArea.setItems(clientsObsvList);
        clientsListArea.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            setText(item);
                            if (item.equals(myNick)) {
                                setStyle("-fx-font-weight: bold;" +
                                        " -fx-background-color: #ffead4");
                            }
                        } else {
                            setGraphic(null);
                            setText(null);
                        }
                    }
                };
            }
        });

        okButtonReg.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> regLoginField.getText().length() == 0
                                || regPassField.getText().length() == 0
                                || regPassField2.getText().length() == 0
                                || regEmailField.getText().length() == 0
                                || !regPassField.getText().equals(regPassField2.getText()),
                        regLoginField.textProperty(),
                        regPassField.textProperty(),
                        regPassField2.textProperty(),
                        regEmailField.textProperty()));


    }

    public void connect() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        String s = in.readUTF();
                        if (s.startsWith("/")) {
                            if (s.startsWith("/authok")) {
                                setAutorized(true);
                                myNick = s.split("\\s")[1];
                                textArea.appendText("успешная авторизация\n");
                                break;
                            }
                            if (s.startsWith("/timeout")) {
                                Platform.runLater(() -> showAlert("Соединение закрыто по таймауту"));
                            }
                            continue;
                        }
                        textArea.appendText(s + "\n");
                    }
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/clients")) {
                            String[] clientsString = msg.substring(9).split(" ");
                            Platform.runLater(() -> {
                                clientsObsvList.clear();
                                clientsObsvList.addAll(clientsString);
                            });
                            continue;
                        }
                        textArea.appendText(msg + "\n");
                    }
                } catch (IOException e) {
                    showAlert("Соединение с сервером разорвано.");
                } finally {
                    setAutorized(false);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (IOException e) {
            showAlert("Не удалось подключиться к серверу. Проверьте сетевое соединение.");
        }
    }

    public void authorization() {
        if (!loginField.getText().isEmpty() && !passFiead.getText().isEmpty()) {
            if (socket == null || socket.isClosed()) connect();
            try {
                out.writeUTF("/auth " +
                        loginField.getText() + " " +
                        passFiead.getText());
                loginField.clear();
                passFiead.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Неполные данные для авторизации!");
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ой! Проблемка нарисавалася!");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void clientChoise(MouseEvent event) {
        if (event.getClickCount() == 2) {
            msgField.setText("/w " + clientsListArea.getSelectionModel().getSelectedItem() + " ");
            msgField.requestFocus();
            msgField.selectEnd();
        }
    }

    public void showRegistrPan(boolean registering) {
        if (registering) {
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            regPanel.setVisible(true);
            regPanel.setManaged(true);
        } else {
            loginPanel.setVisible(true);
            loginPanel.setManaged(true);
            regPanel.setVisible(false);
            regPanel.setManaged(false);
        }
    }

    public void onShowReg() {
        showRegistrPan(true);
    }

    public void offShowReg() {
        showRegistrPan(false);
    }

}
