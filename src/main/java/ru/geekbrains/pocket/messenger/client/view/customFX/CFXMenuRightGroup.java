package ru.geekbrains.pocket.messenger.client.view.customFX;

import ru.geekbrains.pocket.messenger.client.view.ChatViewController;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;


public class CFXMenuRightGroup extends AnchorPane {


    @FXML
    private AnchorPane CFXMenuRightPane;
    @FXML
    private JFXButton btnGroupProfile;
    @FXML
    private JFXButton btnClearHistory;
    @FXML
    private JFXButton btnNotification;
    @FXML
    private JFXButton btnLeaveGroup;
    @FXML
    private JFXButton btnDeleteGroup;

    public CFXMenuRightGroup() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/CFXMenuRightGroup.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            btnGroupProfile.setOnAction(event -> btnGroupProfileClicked());
            btnClearHistory.setOnAction(event -> btnClearHistoryClicked());
            btnNotification.setOnAction(event -> btnNotificationClicked());
            btnLeaveGroup.setOnAction(event -> btnLeaveGroupClicked());
            btnDeleteGroup.setOnAction(event -> btnDeleteGroupClicked());
        } catch (IOException exception){
            throw new RuntimeException(exception);
        }

    }

    private void btnDeleteGroupClicked() {
    }

    private void btnLeaveGroupClicked() {
    }

    private void btnNotificationClicked() {
    }

    private void btnClearHistoryClicked() {
    }

    private void btnGroupProfileClicked() {
    }


    private static ChatViewController parentController;

    public static void setParentController(ChatViewController parent){
        parentController=parent;
    }




}
