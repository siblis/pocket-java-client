package client.view;

import client.controller.ClientController;
import client.utils.Common;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LogonRestorePasswordViewController implements Initializable {

    @FXML
    private TextField emailField;

    @FXML
    private Button recoveryButton;

    @FXML
    private Label codeSendMessageLabel;

    @FXML
    private TextField codeRecovery;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label passwordDoNotMatchLabel;

    @FXML
    private PasswordField newRepeatPasswordField;

    @FXML
    private Button changePassword;

    private ClientController controller;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //controller = ClientController.getInstance();
        Platform.runLater(() -> emailField.requestFocus());
        codeSendMessageLabel.setVisible(false);
        codeRecovery.setDisable(true);
        newPasswordField.setDisable(true);
        newRepeatPasswordField.setDisable(true);
        changePassword.setDisable(true);
        passwordDoNotMatchLabel.setVisible(false);
    }

    @FXML
    private void handleRestorePasswordButton() throws IOException {
        //если забыли пароль, пишем в наш чат
        //в рамках MVP
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Забыли пароль?");
        alert.setHeaderText("Забыли пароль?");
        alert.setContentText("Свяжитесь с техподдержкой: \n" +
                "tg://join?invite=EY3mdg8Lip-U6hQw_ZNtzg");

        alert.showAndWait();

        recoveryButton.setDisable(true);
        codeSendMessageLabel.setVisible(true);
        codeRecovery.setDisable(false);
        newPasswordField.setDisable(false);
        newRepeatPasswordField.setDisable(false);
        changePassword.setDisable(false);

    }

    @FXML
    private void handleChangePassword() throws IOException {
        comparePasswords(newPasswordField.getText(), newRepeatPasswordField.getText());

        Stage stage = (Stage) changePassword.getScene().getWindow();
        stage.close();
    }

    private void comparePasswords(String password1, String password2) {
        passwordDoNotMatchLabel.setVisible(true);

    }
}
