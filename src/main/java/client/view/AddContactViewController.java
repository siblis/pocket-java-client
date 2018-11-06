package client.view;

import client.controller.ClientController;
import client.utils.Correct;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class AddContactViewController implements Initializable {

    @FXML
    TextField addContactTextField;

    @FXML
    Label searchResultLabel;

    @FXML
    Button searchButton;

    @FXML
    Button cancelButton;

    private ClientController controller;
    private String searchResult;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controller = ClientController.getInstance();
        Platform.runLater(() -> addContactTextField.requestFocus());
        searchButton.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> addContactTextField.getText().length() == 0
                                || !Correct.isValidEmail(addContactTextField.getText()),
                        addContactTextField.textProperty()));
    }

    @FXML
    public void handleSearchButton() {
        searchResult = controller.searchContactByEmail(addContactTextField.getText());

        if (!searchResult.equals("404") && !searchResult.equals("500")) {
            searchResultLabel.setText(controller.convertContactToCFS(searchResult).getEmail() + " (нажмите для добавления)");
            searchResultLabel.setCursor(Cursor.HAND);
        } else {
            searchResultLabel.setText("Пользователь не найден.");
            searchResultLabel.setCursor(Cursor.DEFAULT);
        }

        searchResultLabel.setVisible(true);
        addContactTextField.requestFocus();
        addContactTextField.clear();
    }

    @FXML
    public void handleCancelButton() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleSearchResultLabel() {
        if (!searchResult.equals("404") && !searchResult.equals("500")) {
            controller.addContactToDB(controller.convertContactToCFS(searchResult));
            searchResultLabel.setText(null);
            addContactTextField.requestFocus();
            addContactTextField.clear();
        }
    }

}
