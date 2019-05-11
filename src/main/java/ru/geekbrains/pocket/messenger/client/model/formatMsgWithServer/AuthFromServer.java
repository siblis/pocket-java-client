package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthFromServer {

    private String token;

    private UserFromServer user;
}
