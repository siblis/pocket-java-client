package client.view;

import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

public abstract class PaneProvider {
    private static ScrollPane profileScrollPane;
    private static HamburgerBackArrowBasicTransition transitionBack;
    private static BorderPane borderPaneMain;

    public static BorderPane getBorderPaneMain() {
        return borderPaneMain;
    }

    public static void setBorderPaneMain(BorderPane borderPaneMain) {
        PaneProvider.borderPaneMain = borderPaneMain;
    }

    public static HamburgerBackArrowBasicTransition getTransitionBack() {
        return transitionBack;
    }

    public static void setTransitionBack(HamburgerBackArrowBasicTransition transitionBack) {
        PaneProvider.transitionBack = transitionBack;
    }

    public static ScrollPane getProfileScrollPane() {
        return profileScrollPane;
    }

    public static void setProfileScrollPane(ScrollPane profileScrollPane) {
        PaneProvider.profileScrollPane = profileScrollPane;
    }
}
