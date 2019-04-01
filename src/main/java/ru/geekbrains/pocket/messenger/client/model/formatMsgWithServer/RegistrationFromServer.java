package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.messenger.client.model.pub.UserPub;

@Getter
@Setter
@NoArgsConstructor
public class RegistrationFromServer {

    String token;

    UserPub user;

}
