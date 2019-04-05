package ru.geekbrains.pocket.messenger.client.view;

import javafx.scene.control.Alert;
import ru.geekbrains.pocket.messenger.client.controller.ClientController;
import ru.geekbrains.pocket.messenger.client.utils.Correct;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static ru.geekbrains.pocket.messenger.client.utils.Common.showAlert;

public class RegisterViewController implements Initializable {

    @FXML
    private TextField regNameField;

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

    @FXML
    private Rectangle rectPass;

    private ClientController controller;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controller = ClientController.getInstance();
        Platform.runLater(() -> regNameField.requestFocus());
        okRegisterButton.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> regNameField.getText().length() == 0
                                || regPasswordField.getText().length() == 0
                                || regPasswordFieldDouble.getText().length() == 0
                                || regEmailField.getText().length() == 0
                                || !regPasswordField.getText().equals(regPasswordFieldDouble.getText())
                                || !Correct.isValidEmail(regEmailField.getText()),
                        regNameField.textProperty(),
                        regPasswordField.textProperty(),
                        regPasswordFieldDouble.textProperty(),
                        regEmailField.textProperty()));
        regPasswordFieldDouble.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> Correct.checkPasswordStrength(regPasswordField.getText()) < 2,
                        regPasswordField.textProperty()));

    }

    @FXML
    private void handleOkRegisterButton() {
        boolean isRegister = controller.proceedRegister(regNameField.getText(), regPasswordField.getText(), regEmailField.getText());
        if (isRegister) {
            showAlert("Вы успешно зарегистрированы", Alert.AlertType.INFORMATION);
            Stage stage = (Stage) okRegisterButton.getScene().getWindow();
            stage.close();
        } else {
            //TODO отобразить сообщение на форме о неудачной регистрации
        }
    }

    @FXML
    private void handleCancelRegisterButton() {
        Stage stage = (Stage) cancelRegisterButton.getScene().getWindow();
        stage.close();
    }

    //метод, отправляющий пользователя читать политику конфиденциальности
//    @FXML
//    public void handleLearnMore() {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//
//        alert.setTitle("Политика конфиденциальности");
//        alert.setHeaderText("Политика конфиденциальности");
//        alert.setContentText("Сейчас вы читаете политику конфиденциальности.\n" +
//                "Читайте, читайте, да повнимательнее....");
//
//        alert.showAndWait();
//    }
    @FXML
    private void handleKeyReleased() {
        String s = regPasswordField.getText();
        int koefHard = Correct.checkPasswordStrength(s);
        System.out.println(koefHard);
        rectPass.setWidth(235 / 5 * koefHard);
        rectPass.setFill(Color.RED);
        if (koefHard > 1) {
            rectPass.setFill(Color.YELLOW);
        }
        if (koefHard > 3) {
            rectPass.setFill(Color.GREEN);
        }
    }

}