package client.view;

import client.Main;
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

public class Tray extends Application {
    private static SystemTray tray;
   // private static TrayIcon icon;

    public static void trayON(Stage stage){
        System.out.println("сворачиваем в трей");
        stage.hide();
    }

    public static void trayOFF(Stage stage){
        if (stage != null) {
            System.out.println("111");
            stage.show();
            stage.toFront();
        }
        System.out.println("выходим из трея");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final TrayIcon trayIcon;
        Platform.setImplicitExit(false);
        if(!SystemTray.isSupported()){
            System.out.println("No system tray support");
        }
        else{
            tray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(new ImageIcon(Tray.class.getResource("/client/images/icon.png")).getImage().getScaledInstance(16, -1, 4));

            ActionListener exitListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Выход из приложения");
                    System.exit(0);
                }
            };

            final JPopupMenu popup = new JPopupMenu();
            JMenuItem exit = new JMenuItem("Exit");
            JMenuItem open = new JMenuItem("Открыть messenger");
            JMenuItem close = new JMenuItem("Свернуть в трей");
            exit.addActionListener(exitListener);
            open.addActionListener(event->Platform.runLater(new Runnable(){@Override public void run() {trayOFF(primaryStage);}}));
            close.addActionListener(event->Platform.runLater(new Runnable(){@Override public void run() {trayON(primaryStage);}}));
            popup.add(exit);
            popup.addSeparator();
            popup.add(open);
            popup.addSeparator();
            popup.add(close);
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

            tray.add(trayIcon);


//            try {tray.add(trayIcon);
//                MenuItem open = new MenuItem("Открыть TaskBar");
//                MenuItem close = new MenuItem("Выйти");
//                trayIcon.addActionListener(event-> Platform.runLater(new Runnable(){@Override public void run() {trayOFF();}}));
//                open.addActionListener(event->Platform.runLater(new Runnable(){@Override public void run() {trayOFF();}}));
//                close.addActionListener(event->trayON());
//                menu.add(open);
//                menu.addSeparator();
//                menu.add(close);
//                trayIcon.setPopupMenu(menu);
//                trayIcon.setToolTip("TEST");}
//            catch (AWTException ex) {System.out.println("Unable to init system tray");}
        }
    }
}
