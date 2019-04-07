package ru.geekbrains.pocket.messenger.client.view.customFX;

import ru.geekbrains.pocket.messenger.client.view.ChatViewController;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;




public class CFXMenuLeft extends AnchorPane {


    @FXML
    private AnchorPane CFXMenuLeftPane;
    @FXML
    private JFXButton btnMyProfile;
    @FXML
    private JFXButton btnNewGroup;
    @FXML
    private JFXButton btnNewSuperGroup;

    public CFXMenuLeft() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/CFXMenuLeft.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            btnMyProfile.setOnAction(event -> btnMyProfileClicked());

            btnNewGroup.setOnAction(event -> btnNewGroupClicked());

            btnNewSuperGroup.setOnAction(event -> btnNewSuperGroupClicked());
        } catch (IOException exception){
            throw new RuntimeException(exception);
        }

    }

    private static ChatViewController parentController;

    public static void setParentController(ChatViewController parent){
        parentController=parent;
    }

    private void btnNewSuperGroupClicked() {

    }

    private void btnNewGroupClicked() {
        parentController.onNewGroupClicked(new ActionEvent());
    }

    private void btnMyProfileClicked() {
        parentController.onMyProfileOpen(new ActionEvent());
    }


}
