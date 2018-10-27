package client.view;

import client.Main;
import client.controller.ClientController;
import client.utils.HTTPSRequest;
import database.dao.DataBaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatViewController implements Initializable {

    @FXML
    private AnchorPane messagePanel;

    @FXML
    private AnchorPane webViewPane;

    @FXML
    private WebView messageWebView = null;

    @FXML
    private ListView<String> contactList;

    @FXML
    private TextField messageField;

    private DataBaseService dbService;
    private static ObservableList<String> contactsObservList;

    private ClientController controller;

    public ChatViewController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controller = ClientController.getInstance();
        dbService = new DataBaseService();
        fillContactList();
        webtest();
    }

    public static ObservableList<String> getContactList() {
        return contactsObservList;
    }

    private void webtest() {
        messageWebView = new WebView();
        controller.webEngine = messageWebView.getEngine();
        controller.webEngine.setJavaScriptEnabled(true);
        webViewPane.getChildren().setAll(messageWebView);
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
                            if (item.equals(controller.getMyNick())) {
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

    @FXML
    private void handleDisconnectButton() {
        Stage stage = (Stage) messagePanel.getScene().getWindow();
        stage.close();
        Main.initRootLayout();
        Main.showOverview();
    }

    @FXML
    private void handleExit() {
        dbService.close();
        controller.disconnect();
        System.exit(0);
    }

    @FXML
    private void handleSendMessage() {
        controller.sendMessage(controller.getSender(), controller.getReceiver(), messageField.getText());
        messageField.clear();
        messageField.requestFocus();
    }

    @FXML
    private void handleClientChoice(MouseEvent event) {
        controller.clientChoice(this.contactList, event);
        messageField.requestFocus();
        messageField.selectEnd();
    }

    @FXML
    private void handleAddContactButton() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/AddContactView.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add contact");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void updateContactList() {
        ClientController.getInstance().updateContactList();

    }
}
