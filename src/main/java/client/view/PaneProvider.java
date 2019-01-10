package client.view;

import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

public abstract class PaneProvider {
    private static ScrollPane myProfileScrollPane;
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

    public static ScrollPane getMyProfileScrollPane() {
        return myProfileScrollPane;
    }

    public static void setMyProfileScrollPane(ScrollPane myProfileScrollPane) {
        PaneProvider.myProfileScrollPane = myProfileScrollPane;
    }
}
