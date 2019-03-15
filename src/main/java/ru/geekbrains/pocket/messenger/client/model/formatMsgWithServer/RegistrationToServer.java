package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationToServer {

    //TODO valid email
    @Size(min = 6, max = 32)
    @JsonProperty("email")
    String email;

    @Size(min = 8, max = 32)
    @JsonProperty("password")
    String password;

    @Size(min = 2, max = 32)
    @JsonProperty("name")
    String name;

}
