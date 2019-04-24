package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageListFromServer {

   private int offset;
   private MessageFromServer[] data;

}
