package client.view.customFX;

import client.view.PaneProvider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;




public class CFXMyProfile extends AnchorPane {

    @FXML
    private AnchorPane myProfilePane;

    public CFXMyProfile() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/fxml/CFXMyProfile.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
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
}
