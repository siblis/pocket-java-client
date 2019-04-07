package ru.geekbrains.pocket.messenger.client.view.customFX;

import ru.geekbrains.pocket.messenger.client.view.ChatViewController;
import ru.geekbrains.pocket.messenger.client.view.PaneProvider;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import ru.geekbrains.pocket.messenger.database.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.io.IOException;

public class CFXMyProfile extends AnchorPane {

    @FXML
    private AnchorPane myProfilePane;
    @FXML
    private Circle circleAvatar;
    @FXML
    private Label labelEmailMyProfile;
    @FXML
    private JFXTextField tfName;
    @FXML
    private JFXTextArea taInfo;
    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXButton btnNameEdit;
    @FXML
    private JFXButton btnInfoEdit;
    @FXML
    private JFXButton btnSendMessage;
    @FXML
    private Label lblInvokation;
    @FXML
    private JFXButton btnInvokationSwitch;
    @FXML
    private JFXButton btnLogout;
    @FXML
    private JFXButton btnDeleteProfile;

    private User user;

    private ChatViewController parentController;

    private Boolean InvokationSwitch = false;

    private void initListeners(){
        btnInfoEdit.setOnAction(event -> btnInfoClicked());
        btnNameEdit.setOnAction(event -> btnNameChangeClicked());
        btnClose.setOnAction(event -> closeButtonPressed());
        btnSendMessage.setOnAction(event -> btnSendMessagePressed());
        btnInvokationSwitch.setOnAction(event -> btnInvokationSwitchPressed());
        btnLogout.setOnAction(event -> btnLogoutPressed());
        btnDeleteProfile.setOnAction(event -> btnDeleteProfilePressed());
    }

    private void btnDeleteProfilePressed() {
        parentController = ChatViewController.getInstance();
        parentController.alarmDeleteProfileExecute();
    }

    private void btnLogoutPressed() {
        parentController = ChatViewController.getInstance();
        parentController.alarmExitProfileExecute();
    }

    private void btnInvokationSwitchPressed() {
        if (InvokationSwitch) {
            AnchorPane.setRightAnchor(btnInvokationSwitch, 16.0);
            InvokationSwitch = false;
        } else {
            AnchorPane.setRightAnchor(btnInvokationSwitch, 30.0);
            InvokationSwitch = true;
        }
    }

    private void btnRoomPressed() {
    }

    private void btnSendMessagePressed() {
    }

    public CFXMyProfile() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/CFXMyProfile.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initListeners();

        } catch (IOException exception){
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void closeButtonPressed(){
      PaneProvider.getProfileScrollPane().setVisible(false);
      PaneProvider.getTransitionBack().setRate(-1);
      PaneProvider.getTransitionBack().play();
    }

    private void btnNameChangeClicked(){
        tfName.setEditable(true);
    }

    private  void btnInfoClicked(){
        taInfo.setEditable(true);
    }

    public void setUser(User user){
        tfName.setText(user.getUserName());
        labelEmailMyProfile.setText(user.getEmail());
    }
}
