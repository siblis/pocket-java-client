package ru.geekbrains.pocket.messenger.client.view;

import ru.geekbrains.pocket.messenger.client.controller.ClientController;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static ru.geekbrains.pocket.messenger.client.Main.primaryStage;


public class Tray {
    private static final Logger trayLogger = LogManager.getLogger(Tray.class.getName());
    private static SystemTray tray;
    public static Stage currentStage;

    public static void trayON(Stage stage){
        System.out.println("сворачиваем в трей");
        stage.hide();
    }

    public static void trayOFF(Stage stage){
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
        System.out.println("выходим из трея");
    }

    public void setTrayIcon()  {
        final TrayIcon trayIcon;
        Platform.setImplicitExit(false);
        if(!SystemTray.isSupported()){
            System.out.println("No system tray support");
        }
        else{
            tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(new ImageIcon(Tray.class.getResource("/client/images/icon.png")).getImage().getScaledInstance(16, -1, 4));
            trayIcon.setToolTip("Меню Pocket");
            final PopupMenu popup = new PopupMenu();
            MenuItem exit = new MenuItem("Выход");
            MenuItem open = new MenuItem("Развернуть");
            MenuItem close = new MenuItem("Свернуть в трей");
            MenuItem quietMode = new MenuItem("Тихий режим");

            ActionListener exitListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ClientController clientController = ClientController.getInstance();
                    if (clientController != null) clientController.disconnect();
                    System.out.println("Выход из приложения");
                    System.exit(0);
                }
            };

            trayIcon.addActionListener(event-> Platform.runLater(new Runnable(){@Override public void run() {trayOFF(currentStage == null?primaryStage:currentStage);}}));
            open.addActionListener(event->Platform.runLater(new Runnable(){@Override public void run() {trayOFF(currentStage == null?primaryStage:currentStage);}}));
            close.addActionListener(event->Platform.runLater(new Runnable(){@Override public void run() {trayON(currentStage == null?primaryStage:currentStage);}}));
            quietMode.addActionListener(event->Platform.runLater(new Runnable(){@Override public void run() {System.out.println("Пока не сделанно");}}));
            exit.addActionListener(exitListener);

            popup.add(open);
            popup.addSeparator();
            popup.add(quietMode);
            popup.addSeparator();
            popup.add(close);
            popup.addSeparator();
            popup.add(exit);

            trayIcon.setPopupMenu(popup);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                trayLogger.error("trayIcon_error", e);
            }
        }
    }
}