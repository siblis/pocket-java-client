package client.controller;

import client.Main;
import javafx.fxml.FXML;

public class RootLayoutController {

    private Main main;

    public void setMain(Main main) {
        this.main = main;
    }

    @FXML
    private void handleProfile() {
        System.out.println("заполняем профиль....");
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
        //setAutorized(false);
        //conn.chatclient.close();
        System.exit(0);
    }
}
