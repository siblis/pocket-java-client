package client.utils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

import static client.Main.primaryStage;


public class Tray {
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

            ActionListener exitListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Выход из приложения");
                    System.exit(0);
                }
            };

            final JPopupMenu popup = new JPopupMenu();
            JMenuItem exit = new JMenuItem("Выход");
            JMenuItem open = new JMenuItem("Развернуть");
            JMenuItem close = new JMenuItem("Свернуть в трей");
            JMenuItem quietMode = new JMenuItem("Тихий режим");

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
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    if (e.isPopupTrigger()){
                        popup.setLocation(e.getX(), e.getY());
                        popup.setInvoker(popup);
                        popup.setVisible(true);
                    }
                }
            });

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }
}
