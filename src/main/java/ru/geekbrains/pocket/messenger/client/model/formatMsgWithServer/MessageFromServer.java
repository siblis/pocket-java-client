package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.messenger.database.entity.Message;

@Getter
@Setter
@NoArgsConstructor
public class MessageFromServer {

    private String id;
    private String type;
    private String sender;
    private String recipient;
    private String group;
    private String text;
    private boolean read;
    private Timestamp sent_at;
    
    public Message toMessageWithoutUsers() {
        return new Message(id, text, sent_at, null, null);
    }
}
