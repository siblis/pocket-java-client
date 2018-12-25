package client.view;

import javafx.scene.control.ScrollPane;

public abstract class PaneProvider {
    private static ScrollPane myProfileScrollPane;

    public static ScrollPane getMyProfileScrollPane() {
        return myProfileScrollPane;
    }

    public static void setMyProfileScrollPane(ScrollPane myProfileScrollPane) {
        PaneProvider.myProfileScrollPane = myProfileScrollPane;
    }
}
