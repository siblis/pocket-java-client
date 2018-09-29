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
    private ListView<String> clientsListArea;

    @FXML
    private VBox regPanel;
    @FXML
    private Button okButtonReg;
    @FXML
    private TextField regLoginField;
    @FXML
    private TextField passFieldReg;
    @FXML
    private TextField passFieldRegDouble;
    @FXML
    private TextField regEmailField;

    private String myNick;

    private Connector conn = null;

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
                                || passFieldReg.getText().length() == 0
                                || passFieldRegDouble.getText().length() == 0
                                || regEmailField.getText().length() == 0
                                || !passFieldReg.getText().equals(passFieldRegDouble.getText()),
                        regLoginField.textProperty(),
                        passFieldReg.textProperty(),
                        passFieldRegDouble.textProperty(),
                        regEmailField.textProperty()));
    }

    private void connect(String token) {
        conn = new Connector(token, this);
    }

    public void authentication() {
        if (!loginField.getText().isEmpty() && !passFiead.getText().isEmpty()) {
            String token;
            String answer = "0";
            String reqJSON = "{" +
                    "\"user\": \"" + loginField.getText() + "\"," +
                    "\"password\": \"" + passFiead.getText() + "\"" +
                    "}";
            try {
                answer = HTTPSRequest.avtorization(reqJSON);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (answer.contains("token")) {
                //тут надо обработать JSON по нармальному
                token = answer.substring(answer.indexOf("token") + 9, answer.indexOf(",") - 1);
                System.out.println("TOKEN " + token);
                setAutorized(true);
                connect(token);
                myNick = loginField.getText();

            }
        } else {
            showAlert("Неполные данные для авторизации!", "Результат");
        }
    }

    public void sendMessage() {
        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        String receiver = myNick.equals("2") ? "3" : "2";
        String mess = "{ \"receiver\":\"" +
                receiver +
                "\", \"message\":\"" +
                "[" + dateFormat.format(dateNow) + "] " + myNick + " :  " +
                msgField.getText() + "\" }";
        System.out.println(mess);
        conn.chatclient.send(mess);
        reciveMessage("[" + dateFormat.format(dateNow) + "] " + msgField.getText());
        msgField.clear();

    }

    void reciveMessage(String message) {
        textArea.appendText(message + "\n");
    }

    private void showAlert(String message, String title) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
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

    public void conn2() {
        setAutorized(true);
        connect("2d1ea610bc493d76");
        myNick = "2";
    }

    public void conn3() {
        setAutorized(true);
        connect("f5b7c119e858b9f3");
        myNick = "3";
    }

    public void exit() {
        setAutorized(false);
        conn.chatclient.close();
    }

    public void registration() {
        String requestJSON = "{" +
                "\"account_name\": \"" + regLoginField.getText() + "\"," +
                "\"email\": \"" + regEmailField.getText() + "\"," +
                "\"password\": \"" + passFieldReg.getText() + "\"" +
                "}";
        try {
            int responseCode = HTTPSRequest.registration(requestJSON);
            if (responseCode == 201) {
                offShowReg();
                showAlert("Вы успешно зарегистрированы", "Результат");
                loginField.setText(regLoginField.getText());
                passFiead.setText(passFieldReg.getText());
            } else
                showAlert("Ошибка регистрации, код: " + responseCode, "Результат");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
