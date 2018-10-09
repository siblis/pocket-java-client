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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private static String token;
    private static ObservableList<String> clientsObsvList = FXCollections.observableArrayList();
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
    private TextField addContact;
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
    @FXML
    Button buttonAdd;
    @FXML
    private WebView webView = null;

    private String myNick;
    private String receiver = "24";

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
        //ObservableList<String> clientsObsvList = FXCollections.observableArrayList();
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
            //String token;
            String answer = "0";
            String reqJSON = "{" +
                    "\"account_name\": \"" + loginField.getText() + "\"," +
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

//        String receiver = myNick.equals("tester2") ? "25" : "24";
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
        if (event.getClickCount() == 1) {
//            msgField.setText("/w " + clientsListArea.getSelectionModel().getSelectedItem() + " ");
            receiver = clientsListArea.getSelectionModel().getSelectedItem();
            showAlert("Сообщения будут отправляться контакту \n"
                    +receiver,"Временное решение");
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
        // id = 24
        myNick = "tester2";
        loginField.setText("tester2");
        passFiead.setText("123");
        authentication();
        receiver ="25";
    }

    public void conn3() {
        //id = 25
        myNick = "tester3";
        loginField.setText("tester3");
        passFiead.setText("123");
        authentication();
        receiver ="24";
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


    public void addContact() {
        String requestJSON = "{" +
                "\"contact\": " + "\"" + addContact.getText() + "\"" +
                "}";
        try {
            int userId = HTTPSRequest.addContact(requestJSON, token);
            if (userId != -1) {
                addToList(userId);
            } else {
                showAlert("Пользователь с email: " + addContact.getText() +
                        " не найден", "Ошибка добавления контакта");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToList(int uid) {
        String id = String.valueOf(uid);
//         в дальнейшем будет добавлен User , а не id юзера
        if (!clientsObsvList.contains(id)) {
            clientsObsvList.add(id);
            showAlert("Контакт " + id + " успешно добавлен", "Добавление контакта");
        } else {
            showAlert("Пользователь " + id + " уже есть в списке ваших контактов", "Ошибка добавления контакта");
        }
    }


    // для будщего, пока не функционирует,
    private void webtest() {
        webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load("http://www.oracle.com/products/index.html");
    }

}
