package client.controller;

import client.Connector;
import client.HTTPSRequest;
import client.Main;
import database.dao.DataBaseService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.net.URL;
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
    //панель сообщений
    @FXML
    private AnchorPane messagePanel;
    @FXML
    private ListView<String> contactList;

    private Main main;

    private DataBaseService dbService;
    private ObservableList<String> contactsObservList;

    private String myNick;
    private String msgArea = "";
    private String receiver = "24";

    private Connector conn = null;

    public TestEnterViewController() {
    }

    public void setMain(Main main) {
        this.main = main;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
