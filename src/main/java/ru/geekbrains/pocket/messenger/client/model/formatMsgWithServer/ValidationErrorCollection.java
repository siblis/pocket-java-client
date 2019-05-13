package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
public class ValidationErrorCollection {

    private String message;

    private List<String> errors;

}
