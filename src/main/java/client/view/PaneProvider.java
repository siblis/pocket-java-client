package client.view;

import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.scene.control.ScrollPane;

public abstract class PaneProvider {
    private static ScrollPane myProfileScrollPane;
    private static HamburgerBackArrowBasicTransition transitionBack;

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
