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

import static client.utils.Common.showAlert;
import static client.utils.Correct.*;

public class LogonRestorePasswordViewController implements Initializable {

    @FXML
    private TextField emailField;

    @FXML
    private Label emailErrorLabel;

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
        controller = ClientController.getInstance();
        Platform.runLater(() -> emailField.requestFocus());
        emailErrorLabel.setVisible(false);
        codeSendMessageLabel.setVisible(false);
        codeRecovery.setDisable(true);
        newPasswordField.setDisable(true);
        newRepeatPasswordField.setDisable(true);
        changePassword.setDisable(true);
        passwordDoNotMatchLabel.setVisible(false);
    }

    @FXML
    private void handleRestorePasswordButton() {
        //проверка введённого Email
        if (isValidEmail(emailField.getText())) {
            emailErrorLabel.setVisible(false);
        } else {
            emailErrorLabel.setVisible(true);
            return;
        }

        //отправляем указанный email на сервер
        //если он там есть, то возращается код ??? и на email отправляется код восстановления
        //TODO установить правильные коды возвратов сервера
        String answer = controller.proceedRestorePassword(emailField.getText());
        if (answer.equals("201")) {
            //ответ сервера об успешной проверке email и отправки кода восстановления
            recoveryButton.setDisable(true);
            codeSendMessageLabel.setText("Код восстановления отправлен на указанный email");
            codeSendMessageLabel.setVisible(true);
            codeRecovery.setDisable(false);
            newPasswordField.setDisable(false);
            newRepeatPasswordField.setDisable(false);
            changePassword.setDisable(false);
        } else if (answer.equals("203")) {
            //ответ сервера об ошибке: указанный email незарегистрирован
            codeSendMessageLabel.setText("Указанный email незарегистрирован");
            codeSendMessageLabel.setVisible(true);
        } else {
            //ответ сервера об ошибке: ???
            //ошибка: нет соединения с сервером
            System.out.println("handleRestorePasswordButton: Ошибка!");
            codeSendMessageLabel.setText("Неизвестная ошибка. Попробуйте ещё раз.");
            codeSendMessageLabel.setVisible(true);
        }
    }

    @FXML
    private void handleNewPasswordField() {
        //TODO добавить поле отображающее сложность пароля
        int strengthPercentage = checkPasswordStrength(newPasswordField.getText());
        if (!newRepeatPasswordField.getText().equals("")) {
            comparePasswords();
        }
    }

    @FXML
    private void handleNewRepeatPasswordField() {
        //TODO добавить поле отображающее сложность пароля
        if (!newPasswordField.getText().equals("")) {
            comparePasswords();
        }
    }

    @FXML
    private void handleChangePassword() throws IOException {
        if (!newPasswordField.getText().equals(newRepeatPasswordField.getText()))
            return;
        //если новый пароль совпадает с повтором, то отправляем его на сервер
        //TODO установить правильные коды возвратов сервера
        String answer = controller.proceedChangePassword(emailField.getText(), codeRecovery.getText(), newPasswordField.getText());
        if (answer.equals("201")) {
            //ответ сервера об успешной смене пароля
            showAlert("Пароль успешно изменён.", Alert.AlertType.INFORMATION);
            //закрываем окно
            Stage stage = (Stage) changePassword.getScene().getWindow();
            stage.close();
        } else if (answer.equals("203")) {
            //ответ сервера об ошибке: указанный код восстановления не подходит
            showAlert("Код восстановления указан не верно!", Alert.AlertType.ERROR);
            codeSendMessageLabel.setText("Код восстановления указан не верно!");
            codeSendMessageLabel.setVisible(true);
        } else {
            //ответ сервера об ошибке: ???
            //ошибка: нет соединения с сервером
            System.out.println("handleRestorePasswordButton: Ошибка!");
            codeSendMessageLabel.setText("Неизвестная ошибка. Попробуйте ещё раз.");
            codeSendMessageLabel.setVisible(true);
        }
    }

    private void comparePasswords() {
        if (newPasswordField.getText().equals(newRepeatPasswordField.getText()))
            passwordDoNotMatchLabel.setVisible(true);
        else
            passwordDoNotMatchLabel.setVisible(false);
    }
}
