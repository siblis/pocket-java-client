package client.view.customFX;

import client.view.PaneProvider;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import database.entity.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.io.IOException;

public class CFXOtherProfile extends AnchorPane {

    private boolean isFriend = false;

    private User user;

    @FXML
    private AnchorPane otherProfilePane;

    @FXML
    private JFXButton btnCloseRight;
    @FXML
    private JFXButton btnCloseCenter;
    @FXML
    private JFXButton btnInvite;
    @FXML
    private Circle circleAvatar;
    @FXML
    private Circle circleOnline;
    @FXML
    private Label labelEmailOtherProfile;
    @FXML
    private Label labelStatus;
    @FXML
    private Label labelName;
    @FXML
    private JFXTextArea textareaInfo;

    @FXML
    private JFXButton writeMsgBtn;

    @FXML
    private JFXButton clearMsgsBtn;

    @FXML
    private JFXButton removeUserBtn;

    public void setOnline(){
        circleOnline.setVisible(true);
        labelStatus.setVisible(true);
    }

    public void setOffline(){
        circleOnline.setVisible(false);
        labelStatus.setVisible(false);
    }

    public void setUser(User user){
        this.user = user;
        labelEmailOtherProfile.setText(user.getEmail());
        labelName.setText(user.getAccount_name());
    }

    public CFXOtherProfile() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/CFXOtherProfile.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            setIfFriendly(false);
            btnCloseRight.setOnAction(event -> closeButtonPressed());
            btnCloseCenter.setOnAction(event -> closeButtonPressed());
        } catch (IOException exception){
            throw new RuntimeException(exception);
        }

    }

    public CFXOtherProfile(User user) {
        this();
        setUser(user);
    }

    private void checkIsFriend() {
        // true для контактов из адресной книги:
        btnCloseCenter.setVisible(isFriend);
        writeMsgBtn.setVisible(isFriend);
        clearMsgsBtn.setVisible(isFriend);
        removeUserBtn.setVisible(isFriend);
        circleOnline.setVisible(isFriend);
        // true для контактов отсутствующих в адресной книге:
        btnCloseRight.setVisible(!isFriend);
        btnInvite.setVisible(!isFriend);
        labelStatus.setVisible(!isFriend);
    }

    public void setIfFriendly(boolean isFriend){
        this.isFriend=isFriend;
        checkIsFriend();
    }

    public boolean isFriend() {
        return isFriend;
    }

    private void closeButtonPressed() {
        PaneProvider.getProfileScrollPane().setVisible(false);
    }
}
