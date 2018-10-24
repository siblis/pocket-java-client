package client.view;

import client.controller.ClientController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class AddContactViewController implements Initializable {

    @FXML
    TextField addContactTextField;

    @FXML
    Button addButton;

    @FXML
    Button cancelButton;

    private ClientController controller;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controller = ClientController.getInstance();
        /*
        Platform.runLater(() -> regLoginField.requestFocus());
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
        */
    }

    @FXML
    public void handleAddButton() {
        controller.addContact(addContactTextField.getText());
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleCancelButton() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
