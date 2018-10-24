package client.view;

import client.controller.ClientController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class RootLayoutController implements Initializable {

    private ClientController controller;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controller = ClientController.getInstance();
    }

    @FXML
    private void handleConnectingSettings() {
        System.out.println("настраиваем связь....");
    }

    @FXML
    private void handleViewSettings() {
        System.out.println("настраиваем внешний вид....");
    }

    @FXML
    private void handleExit() {
        controller.disconnect();
        System.exit(0);
    }
}
