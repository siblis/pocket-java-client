package client.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class ContactCustomSceneController implements Initializable {

    @FXML
    private ImageView avatar;

    @FXML
    private Label nameLabel;

    @FXML
    private Label lastMessageLabel;

    @FXML
    private Label messageDateLabel;

    @FXML
    private Label isGetMessageLabel;
    //переменные, определяющие доставлено ли и прочитано ли сообщение
    private boolean isDelivered;
    private boolean isRead;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
