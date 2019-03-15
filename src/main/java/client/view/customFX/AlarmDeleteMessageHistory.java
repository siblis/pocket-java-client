package client.view.customFX;

import client.controller.ClientController;
import client.view.PaneProvider;
import client.view.Profile;
import com.jfoenix.controls.JFXButton;
import database.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AlarmDeleteMessageHistory extends AnchorPane {

    @FXML
    private JFXButton btnConfirm;

    @FXML
    private  JFXButton btnDecline;

    Stage dialogStage=null;
    String profile;
    User user;
    Profile prof;

    public AlarmDeleteMessageHistory(Profile prof, User user) {

        this.prof = prof;
        this.user = user;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/AlarmWindowDeleteMessageHistory.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            AnchorPane root = fxmlLoader.load();
            dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(PaneProvider.getBorderPaneMain().getScene().getWindow());
            Scene scene = new Scene(root);
            scene.setFill(null);
            dialogStage.setScene(scene);
            dialogStage.show();
            initializeListeners();
        } catch (IOException exception){
            throw new RuntimeException(exception);
        }

    }

    private void initializeListeners() {
        btnConfirm.setOnAction(event -> btnConfirmOnPressed());
        btnDecline.setOnAction(event -> btnDeclinePressed());
    }

    private void btnDeclinePressed() {
        dialogStage.close();
    }

    private void btnConfirmOnPressed() {
        switch (prof) {
            case MY:  // todo очистку?
                System.out.println("Очистка истории пользователя"); //сообщение для контроля и тестирования
                break;
            case OTHER:
                System.out.println("Очистка истории контакта"); //сообщение для контроля и тестирования
                ClientController.getInstance().clearMessagesWithUser(user); // todo отображения пустого списка сообщений до закрытия контакта?
                break;
        }
        dialogStage.close();

    }
}
