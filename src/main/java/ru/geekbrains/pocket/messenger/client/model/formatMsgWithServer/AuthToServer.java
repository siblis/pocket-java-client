package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthToServer {

    @Size(min = 6, max = 32)
    public String email;
    @Size(min = 8, max = 32)
    public String password;

    public String toJson() {
        return new Gson().toJson(this);
    }

}
