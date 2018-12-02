package client.view;

import client.Main;
import client.controller.ClientController;
import client.customFX.CFXListElement;
import client.utils.HTTPSRequest;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatViewController implements Initializable {

    @FXML
    private AnchorPane messagePanel;

    @FXML
    private AnchorPane webViewPane;

    @FXML
    private WebView messageWebView;

    @FXML
    private JFXListView<CFXListElement> contactList;

    @FXML
    private TextField messageField;

    private static ObservableList<CFXListElement> contactsObservList;

    private ClientController controller;

    public ChatViewController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controller = ClientController.getInstance();
        contactList.setExpanded(true);
        controller.initContactFIL();
        fillContactList();
        webtest();
    }

    public static ObservableList<CFXListElement> getContactList() {
        return contactsObservList;
    }

    private void webtest() {
        controller.webEngine = messageWebView.getEngine();
        controller.webEngine.setJavaScriptEnabled(true);
        ClientController.getInstance().updateContactList();
    }

    private void fillContactList() {
        contactsObservList = FXCollections.observableArrayList();
        contactList.setItems(contactsObservList);
        contactList.setCellFactory(new Callback<ListView<CFXListElement>, ListCell<CFXListElement>>() {
            @Override
            public ListCell<CFXListElement> call(ListView<CFXListElement> param) {
                return new JFXListCell<CFXListElement>(){
                    @Override
                    public void updateItem(CFXListElement item, boolean empty) {
                        super.updateItem(item, empty);
//                        if (!empty) {
//                            setText(controller.);
//                            if (item.equals(controller.getMyNick())) {
//                                setStyle("-fx-font-weight: bold;" +
//                                        " -fx-background-color: #ffead4");
//                            }
//                        } else {
//
//                            setText(null);
//                        }
                    }
                };
            }
        });
        contactsObservList.clear();
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
        controller.disconnect();
        System.exit(0);
    }

    @FXML
    private void handleSendMessage() {
        if (messageField.getText().length() > 0) {
            controller.sendMessage(controller.getSender(), controller.getReceiver(), messageField.getText());
            messageField.clear();
            messageField.requestFocus();
        }
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
