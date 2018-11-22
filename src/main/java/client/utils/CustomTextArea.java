package client.utils;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
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
        //TODO починить установку фокуса
        //Сейчас при установке фокуса пропадает возможность ввода текста, до тех пор, пока не будет выбран получатель в контакт-листе
        //setFocused(true);
        ScrollPane scrollPane = (ScrollPane)lookup(".scroll-pane");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        StackPane viewport = (StackPane) scrollPane.lookup(".viewport");
        Region content = (Region) viewport.lookup(".content");
        content.setPadding(new Insets(DEFAULT_MIN_HEIGHT / 2 - 10, 0, DEFAULT_MIN_HEIGHT / 2 - 10, 0));
        Font font = new Font(14);
        setFont(font);
        Text text = (Text) content.lookup(".text");

        text.textProperty().addListener((property) -> {
            double textHeight = text.getBoundsInLocal().getHeight();
            if (textHeight < DEFAULT_MAX_HEIGHT) {
                setMinHeight(textHeight + DEFAULT_MIN_HEIGHT / 2 + 5);
                setPrefHeight(textHeight + DEFAULT_MIN_HEIGHT / 2 + 5);
                setMaxHeight(textHeight + DEFAULT_MIN_HEIGHT / 2 + 5);
            }
        });

    }
}