package client;

import database.dao.DataBaseService;
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
    @FXML
    private HBox centerPanel;
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
    private ListView<String> contactList;
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

    private DataBaseService dbService;
    private ObservableList<String> contactsObservList;

    @FXML
    private WebView messageView = null;
    private WebEngine webEngine = null;

    private String myNick;
    private String msgArea = "";
    private String receiver = "24";

    private Connector conn = null;

    private void setAutorized(boolean autorized) {
        if (autorized) {
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            messagePanel.setVisible(true);
            messagePanel.setManaged(true);
            centerPanel.setVisible(true);
            centerPanel.setManaged(true);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAutorized(false);
        dbService = new DataBaseService();

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
                myNick + " [" + dateFormat.format(dateNow) + "]: " +
                msgField.getText() + "\" }";
        System.out.println(mess);
        conn.chatclient.send(mess);
        reciveMessage(myNick + " [" + dateFormat.format(dateNow) + "]: " + msgField.getText());
        msgField.clear();

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
//            msgField.setText("/w " + contactList.getSelectionModel().getSelectedItem() + " ");
            receiver = contactList.getSelectionModel().getSelectedItem();
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
        dbService.close();
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
    
    private void webtest() {
        messageView = new WebView();
        webEngine = messageView.getEngine();
        centerPanel.getChildren().add(0, messageView);
    }

}
