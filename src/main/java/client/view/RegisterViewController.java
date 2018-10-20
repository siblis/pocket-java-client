package client.view;

import client.controller.ClientController;
import client.utils.Correct;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterViewController implements Initializable {

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

    @FXML
    private Button cancelRegisterButton;

    private ClientController controller;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controller = ClientController.getInstance();
        Platform.runLater(() -> regLoginField.requestFocus());
        okRegisterButton.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> regLoginField.getText().length() == 0
                                || regPasswordField.getText().length() == 0
                                || regPasswordFieldDouble.getText().length() == 0
                                || regEmailField.getText().length() == 0
                                || !regPasswordField.getText().equals(regPasswordFieldDouble.getText())
                                || !Correct.isValidEmail(regEmailField.getText()),
                        regLoginField.textProperty(),
                        regPasswordField.textProperty(),
                        regPasswordFieldDouble.textProperty(),
                        regEmailField.textProperty()));
    }

    @FXML
    private void handleOkRegisterButton() {
        controller.proceedRegister(regLoginField.getText(), regPasswordField.getText(), regEmailField.getText());
        Stage stage = (Stage) okRegisterButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancelRegisterButton() {
        Stage stage = (Stage) cancelRegisterButton.getScene().getWindow();
        stage.close();
    }
}