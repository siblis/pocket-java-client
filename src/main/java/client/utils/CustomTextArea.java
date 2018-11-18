package client.utils;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CustomTextArea extends TextArea {

    private final double DEFAULT_MIN_HEIGHT = 50.0;
    private final double DEFAULT_MAX_HEIGHT = 350.0;

    public CustomTextArea() {
        setMinHeight(DEFAULT_MIN_HEIGHT);
        setPrefHeight(DEFAULT_MIN_HEIGHT);
        setMaxHeight(DEFAULT_MIN_HEIGHT);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        setWrapText(true);
        setPadding(new Insets(0, 120, 0, 69));//паддиинги в соответствии с дизайном

        ScrollPane scrollPane = (ScrollPane)lookup(".scroll-pane");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPadding(new Insets(0, 0, 0, 0));

        StackPane viewport = (StackPane) scrollPane.lookup(".viewport");
        viewport.setPadding(new Insets(0, 0, 0, 0));

        Region content = (Region) viewport.lookup(".content");
        content.setPadding(new Insets(-1, 1, 0, 1));

        Text text = (Text) content.lookup(".text");

        text.textProperty().addListener((property) -> {
            double textHeight = text.getBoundsInLocal().getHeight();

            if (textHeight < DEFAULT_MAX_HEIGHT) {

                if (textHeight < DEFAULT_MIN_HEIGHT) {
                    textHeight = DEFAULT_MIN_HEIGHT;
                }

                textHeight = textHeight + 1;
                setMinHeight(textHeight);
                setPrefHeight(textHeight);
                setMaxHeight(textHeight);
            }
        });
    }
}