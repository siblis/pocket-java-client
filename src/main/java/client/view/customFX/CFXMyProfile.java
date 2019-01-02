package client.view.customFX;

import client.view.PaneProvider;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import database.entity.User;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.event.ActionEvent;

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


    private User user;

    public CFXMyProfile() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/CFXMyProfile.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            btnInfoEdit.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                 btnInfoClicked();
                }
            });

            btnNameEdit.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                 btnNameChangeClicked();
                }
            });
        } catch (IOException exception){
            throw new RuntimeException(exception);
        }

    }

    @FXML
    private void closeButtonPressed(){
      PaneProvider.getMyProfileScrollPane().setVisible(false);
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
