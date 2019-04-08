package ru.geekbrains.pocket.messenger.client.view.customFX;

import ru.geekbrains.pocket.messenger.client.controller.ClientController;
import ru.geekbrains.pocket.messenger.client.view.PaneProvider;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.geekbrains.pocket.messenger.client.view.ProfileType;
import ru.geekbrains.pocket.messenger.database.entity.User;

import java.io.IOException;

public class AlarmDeleteProfile extends AnchorPane {

    @FXML
    private JFXButton btnConfirm;

    @FXML
    private  JFXButton btnDecline;

    Stage dialogStage=null;

    String profile;
    User user;
    ProfileType prof;

    public AlarmDeleteProfile(ProfileType prof, User user) {

        this.prof = prof;
        this.user = user;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/AlarmWindowDeleteProfile.fxml"));
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
        dialogStage.close();
        switch (prof) {
            case MY:
                //todo: допилить удаление профиля (с БД)
                break;
            case OTHER:
                ClientController.getInstance().removeContact(user);
                break;
        }
    }
}
