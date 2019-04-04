package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationToServer {

    //TODO valid email
    @Size(min = 6, max = 32)
    String email;

    @Size(min = 8, max = 32)
    String password;

    @Size(min = 2, max = 32)
    String name;

}
