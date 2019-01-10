package client.view.customFX;

import client.model.Group;
import client.view.PaneProvider;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.IOException;




public class CFXGroupProfile extends AnchorPane {

@FXML
private Circle circleAvatar;

@FXML
private JFXButton btnNameEdit;

@FXML
private JFXButton btnInfoEdit;

@FXML
private JFXTextArea taInfo;

@FXML
private JFXButton btnShowMembers;
@FXML
private JFXButton btnNotifications;
@FXML
private JFXButton btnClearHistory;
@FXML
private JFXButton btnAddUserToGroup;
@FXML
private JFXButton btnLeaveGroup;
@FXML
private JFXButton btnDismissUser;
@FXML
private JFXButton btnClose;
@FXML
private AnchorPane groupProfilePane;
@FXML
private JFXTextField tfGroupName;


    private Group group;

    public CFXGroupProfile() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/CFXGroupProfile.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            initializeListeners();
            setAvatar(new Image("/client/images/groupAvatars/randomGroupAva.png"));
        } catch (IOException exception){
            throw new RuntimeException(exception);
        }

    }

    private void initializeListeners(){
        btnNameEdit.setOnAction(event -> btnNameEditClicked());
        btnInfoEdit.setOnAction(event -> btnInfoEditClicked());
        btnShowMembers.setOnAction(event -> btnShowMembersClicked());
        btnNotifications.setOnAction(event -> btnNotificationsClicked());
        btnClearHistory.setOnAction(event -> btnClearHistoryClicked());
        btnAddUserToGroup.setOnAction(event -> btnAddUserToGroupClicked());
        btnLeaveGroup.setOnAction(event -> btnLeaveGroupClicked());
        btnDismissUser.setOnAction(event -> btnDismissUserClicked());
        btnClose.setOnAction(event -> btnCloseClicked());
    }

    private void btnCloseClicked() {
        PaneProvider.getProfileScrollPane().setVisible(false);
    }

    private void btnDismissUserClicked() {

    }

    private void btnLeaveGroupClicked() {

    }

    private void btnAddUserToGroupClicked() {

    }

    private void btnClearHistoryClicked() {
    }

    private void btnNotificationsClicked() {

    }

    private void btnShowMembersClicked() {

    }

    private void btnInfoEditClicked() {
        taInfo.setEditable(true);
    }

    public void setGroup(Group group){
       this.group = group;
       uploadFromGroup(group);
    }

    private void btnNameEditClicked() {
        tfGroupName.setEditable(true);
    }

    private void uploadFromGroup(Group group){
        tfGroupName.setText(group.getGroup_name());

    }

    public void setAvatar(Image avatar) {
        this.circleAvatar.setFill(new ImagePattern(avatar));

    }
}
