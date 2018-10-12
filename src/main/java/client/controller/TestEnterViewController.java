package client.controller;

import client.Connector;
import client.HTTPSRequest;
import database.dao.DataBaseService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class TestEnterViewController implements Initializable {
    private static String token;
    //панель входа
    @FXML
    private AnchorPane loginPanel;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    //панель регистрации
    @FXML
    private AnchorPane regPanel;
    @FXML
    private TextField regLoginField;
    @FXML
    private PasswordField regPasswordField;
    @FXML
    private PasswordField regPasswordFieldDouble;
    @FXML
    private TextField regEmailField;
    @FXML
    private Button okRegisterButton;
    //панель сообщений
    @FXML
    private AnchorPane messagePanel;
    @FXML
    private WebView messageWebView = null;
    @FXML
    private ListView<String> contactList;
    @FXML
    private TextField messageField;
    @FXML
    private TextField addContact;

    private WebEngine webEngine = null;

    private DataBaseService dbService;
    private ObservableList<String> contactsObservList;

    private String myNick;
    private String msgArea = "";
    private String receiver = "24";

    private Connector conn = null;

    public TestEnterViewController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAutorized(false);
        dbService = new DataBaseService();

        okRegisterButton.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> regLoginField.getText().length() == 0
                                || regPasswordField.getText().length() == 0
                                || regPasswordFieldDouble.getText().length() == 0
                                || regEmailField.getText().length() == 0
                                || !regPasswordField.getText().equals(regPasswordFieldDouble.getText()),
                        regLoginField.textProperty(),
                        regPasswordField.textProperty(),
                        regPasswordFieldDouble.textProperty(),
                        regEmailField.textProperty()));
    }

    private void setAutorized(boolean autorized) {
        if (autorized) {
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            messagePanel.setVisible(true);
            messagePanel.setManaged(true);
            //centerPanel.setVisible(true);
            //centerPanel.setManaged(true);
            fillContactList();
            webtest();
        } else {
            loginPanel.setVisible(true);
            loginPanel.setManaged(true);
            messagePanel.setVisible(false);
            messagePanel.setManaged(false);
            myNick = "";
        }

    }

    private void webtest() {
        messageWebView = new WebView();
        webEngine = messageWebView.getEngine();
        messagePanel.getChildren().add(0, messageWebView);
    }

    private void fillContactList() {
        contactsObservList = FXCollections.observableArrayList();
        contactList.setItems(contactsObservList);

        contactList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
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

        contactsObservList.clear();
        contactsObservList.addAll(dbService.getAllUserNames());
    }

    private void connect(String token) {
        conn = new Connector(token,this);
    }

    //методы, обрабатывающие нажатие на кнопки
    @FXML
    private void autentification() {
        if (!loginField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
            //String token;
            String answer = "0";
            String reqJSON = "{" +
                    "\"account_name\": \"" + loginField.getText() + "\"," +
                    "\"password\": \"" + passwordField.getText() + "\"" +
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

    private void showAlert(String message, String title) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
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

    @FXML
    public void onShowReg() {
        showRegisterPan(true);
    }
    @FXML
    public void offShowReg() {
        showRegisterPan(false);
    }

    @FXML
    private void handleGuestC2() {
        // id = 24
        myNick = "tester2";
        loginField.setText("tester2");
        passwordField.setText("123");
        autentification();
        receiver ="25";

    }
    @FXML
    private void handleGuestC3() {
        //id = 25
        myNick = "tester3";
        loginField.setText("tester3");
        passwordField.setText("123");
        autentification();
        receiver ="24";

    }

    @FXML
    private void handleRegister() {
        String requestJSON = "{" +
                "\"account_name\": \"" + regLoginField.getText() + "\"," +
                "\"email\": \"" + regEmailField.getText() + "\"," +
                "\"password\": \"" + regPasswordField.getText() + "\"" +
                "}";
        try {
            int responseCode = HTTPSRequest.registration(requestJSON);
            if (responseCode == 201) {
                offShowReg();
                showAlert("Вы успешно зарегистрированы", "Результат");
                loginField.setText(regLoginField.getText());
                passwordField.setText(regPasswordField.getText());
            } else
                showAlert("Ошибка регистрации, код: " + responseCode, "Результат");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSendMessage() {
        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

//        String receiver = myNick.equals("tester2") ? "25" : "24";
        String mess = "{ \"receiver\":\"" +
                receiver +
                "\", \"message\":\"" +
                myNick + " [" + dateFormat.format(dateNow) + "]: " +
                messageField.getText() + "\" }";
        System.out.println(mess);
        conn.chatclient.send(mess);
        reciveMessage(myNick + " [" + dateFormat.format(dateNow) + "]: " + messageField.getText());
        messageField.clear();
    }

    void reciveMessage(String message) {
        msgArea += message + "<br>";
        webEngine.loadContent(  "<html>" +
                "<body>" +
                "<p style=\"font-size: 16px\">" +
                msgArea +
                "<script>" +
                "javascript:scroll(0,10000)" +
                "</script>"+
                "</p>" +
                "<body>" +
                "</html>");
    }

    public void clientChoice(MouseEvent event) {
        if (event.getClickCount() == 1) {
//            msgField.setText("/w " + contactList.getSelectionModel().getSelectedItem() + " ");
            receiver = contactList.getSelectionModel().getSelectedItem();
            showAlert("Сообщения будут отправляться контакту \n"
                    +receiver,"Временное решение");
            messageField.requestFocus();
            messageField.selectEnd();
        }
    }

    @FXML
    public void exit() {
        setAutorized(false);
        conn.chatclient.close();
        dbService.close();
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

    private void addToList(int uid) {
        String id = String.valueOf(uid);
//         в дальнейшем будет добавлен User , а не id юзера
        if (!contactsObservList.contains(id)) {
            contactsObservList.add(id);
            showAlert("Контакт " + id + " успешно добавлен", "Добавление контакта");
        } else {
            showAlert("Пользователь " + id + " уже есть в списке ваших контактов", "Ошибка добавления контакта");
        }
    }


}
