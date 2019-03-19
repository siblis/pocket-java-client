package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.messenger.client.model.pub.UserPub;

@Getter
@Setter
@NoArgsConstructor
public class RegistrationFromServer {

    @JsonProperty("token")
    String token;

    @JsonProperty("user")
    UserPub user;

}
