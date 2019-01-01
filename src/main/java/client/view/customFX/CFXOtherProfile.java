package client.view.customFX;

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
    private AnchorPane myProfilePane;

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
            checkIsFriend();
        } catch (IOException exception){
            throw new RuntimeException(exception);
        }

    }

    private void checkIsFriend() {
        if (isFriend) {
            btnCloseCenter.setVisible(true);
            btnCloseRight.setVisible(false);
            btnInvite.setVisible(false);
        } else {
            btnCloseCenter.setVisible(false);
            btnCloseRight.setVisible(true);
            btnInvite.setVisible(true);
        }
    }



    public void setIfFriendly(boolean isFriend){
        this.isFriend=isFriend;
    }

    public boolean isFriend() {
        return isFriend;
    }


}
