package client.view;

import client.Main;
import client.controller.ClientController;
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
    private ListView<String> contactListView;

    @FXML
    private TextField messageField;

    private ObservableList<String> contactsObservList;

    private ClientController clientController;

    public ChatViewController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clientController = ClientController.getInstance();
        clientController.setChatViewController(this);
        contactsObservList = FXCollections.observableArrayList();
        fillContactListView();
        webtest();
    }

    private void webtest() {
        messageWebView = new WebView();
        clientController.webEngine = messageWebView.getEngine();
        clientController.webEngine.setJavaScriptEnabled(true);
        webViewPane.getChildren().setAll(messageWebView);
    }

    public void fillContactListView() {
        contactListView.setItems(contactsObservList);
        contactListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            setText(item);
                            if (item.equals(clientController.getMyNick())) {
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
        contactsObservList.addAll(clientController.getAllUserNames());
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
        clientController.dbServiceClose();
        clientController.disconnect();
        System.exit(0);
    }

    @FXML
    private void handleSendMessage() {
        clientController.sendMessage(clientController.getSender(), clientController.getReceiver(), messageField.getText());
        messageField.clear();
        messageField.requestFocus();
    }

    @FXML
    private void handleClientChoice(MouseEvent event) {
        clientController.clientChoice(this.contactListView, event);
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
}
