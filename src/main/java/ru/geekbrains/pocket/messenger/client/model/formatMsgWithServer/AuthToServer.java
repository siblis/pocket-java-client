package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthToServer {

    @Size(min = 6, max = 32)
    public String email;
    @Size(min = 8, max = 32)
    public String password;
}
