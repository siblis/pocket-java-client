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

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private Session session;

    private String myNick;

    private void setAutorized(boolean autorized) {
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
        setAutorized(false); // временно изменено на true
        ObservableList<String> clientsObsvList = FXCollections.observableArrayList();
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

    private void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            String uri = "ws://echo.websocket.org:80/";
            System.out.println("Connecting to " + uri);
            session = container.connectToServer(new MyClientEndpoint(this), URI.create(uri));
        } catch (DeploymentException | IOException e) {
            e.printStackTrace();
        }
    }

    public void authentication() {
        if (!loginField.getText().isEmpty() && !passFiead.getText().isEmpty()) {
            if (session == null || !(session.isOpen()))
                connect();
            try {   // имитация аутентификации
                session.getBasicRemote().sendText("/auth " +
                        loginField.getText() + " " +
                        passFiead.getText());
                loginField.clear();
                passFiead.clear();

                textArea.appendText("успешная авторизация\n");
                setAutorized(true);
                myNick = "MyNick";
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Неполные данные для авторизации!");
        }
    }

    public void sendMessage() {
        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String completeMessage = dateFormat.format(dateNow) + " : " + msgField.getText();
        try {
            session.getBasicRemote().sendText(completeMessage);
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void reciveMessage(String message){
        textArea.appendText(message + "\n");
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ой! Проблемка нарисавалася!");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void clientChoice(MouseEvent event) {
        if (event.getClickCount() == 2) {
            msgField.setText("/w " + clientsListArea.getSelectionModel().getSelectedItem() + " ");
            msgField.requestFocus();
            msgField.selectEnd();
        }
    }

    private void showRegisterPan(boolean registering) {
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
        showRegisterPan(true);
    }

    public void offShowReg() {
        showRegisterPan(false);
    }

}
