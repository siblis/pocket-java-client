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
}
