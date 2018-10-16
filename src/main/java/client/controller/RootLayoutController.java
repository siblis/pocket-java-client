package client.controller;

import client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

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

    @FXML
    public void handleAddContact() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("AddContactView.fxml"));
            AnchorPane pane = (AnchorPane) loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Contact");
            stage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(pane);
            stage.setScene(scene);

            TestEnterViewController controller = loader.getController();

            //controller.addContact();

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
