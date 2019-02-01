package client.view.customFX;

import client.view.ChatViewController;
import client.view.PaneProvider;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import database.entity.User;
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
    private JFXButton btnRoom;
    @FXML
    private JFXButton btnInvokation;
    @FXML
    private JFXButton btnLogout;
    @FXML
    private JFXButton btnDeleteHistory;
    @FXML
    private JFXButton btnDeleteProfile;


    private User user;

    private ChatViewController parentController;

    private void initListeners(){
        btnInfoEdit.setOnAction(event -> btnInfoClicked());
        btnNameEdit.setOnAction(event -> btnNameChangeClicked());
        btnClose.setOnAction(event -> closeButtonPressed());
        btnSendMessage.setOnAction(event -> btnSendMessagePressed());
        btnRoom.setOnAction(event -> btnRoomPressed());
        btnInvokation.setOnAction(event -> btnInvokationPressed());
        btnLogout.setOnAction(event -> btnLogoutPressed());
        btnDeleteHistory.setOnAction(event -> btnDeleteHistoryPressed());
        btnDeleteProfile.setOnAction(event -> btnDeleteProfilePressed());
    }

    private void btnDeleteProfilePressed() {
        parentController = ChatViewController.getInstance();
        parentController.alarmDeleteProfileExecute();
    }

    private void btnDeleteHistoryPressed() {
        parentController = ChatViewController.getInstance();
        parentController.alarmDeleteMessageHistoryExecute();
    }

    private void btnLogoutPressed() {
        parentController = ChatViewController.getInstance();
        parentController.alarmExitProfileExecute();
    }

    private void btnInvokationPressed() {
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
        tfName.setText(user.getAccount_name());
        labelEmailMyProfile.setText(user.getEmail());
    }


}
