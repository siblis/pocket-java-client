package client.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import client.controller.ClientController;
import client.utils.Tray;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LogonViewController implements Initializable {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button logInButton;

    private ClientController controller;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controller = ClientController.getInstance();
        Platform.runLater(() -> loginField.requestFocus());
    }

    private void handleLogIn(String login, String password) throws IOException {
        if (controller.proceedLogIn(login, password)) {
            Stage stage = (Stage) logInButton.getScene().getWindow();
            stage.close();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/ChatView.fxml"));
            Parent root = fxmlLoader.load();
            Stage chatStage = new Stage();
            Platform.setImplicitExit(false);
            chatStage.getIcons().add(new Image(getClass().getResourceAsStream("/client/images/icon.png")));
            chatStage.setMinWidth(750.0);
            chatStage.setMinHeight(430.0);
            chatStage.setTitle("Pocket desktop client. \t\t Logged as: [" + controller.getSenderName() + "]");
            chatStage.setScene(new Scene(root));
            chatStage.show();
            Tray.currentStage = chatStage;

            chatStage.setOnCloseRequest(event -> {
                event.consume();
                Tray.trayON(chatStage);
            });
        } else {
            loginField.clear();
            passwordField.clear();
            loginField.requestFocus();
        }
    }

    @FXML
    private void handleLogInButton() throws IOException {
        handleLogIn(loginField.getText(), passwordField.getText());
    }

    @FXML
    private void handleGuestC2Button() throws IOException {
        // id = 24
        handleLogIn("tester2", "123");
        controller.setReceiver(25L);
    }

    @FXML
    private void handleGuestC3Button() throws IOException {
        //id = 25
        handleLogIn("tester3", "123");
        controller.setReceiver(24L);
    }

    @FXML
    private void handleRegisterButton() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/RegisterView.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("New account registration");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/client/images/icon.png")));
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }

    //метод для вспоминания пароля
    public void handleRememberPassword() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/LoginRememberPasswordView.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Remember and change password");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/client/images/icon.png")));
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
