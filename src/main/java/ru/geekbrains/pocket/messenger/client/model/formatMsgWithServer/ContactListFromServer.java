package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ContactListFromServer {

    private String user; //id пользователя которому принадлежит коллекция!?

    private int offset;

    private ContactFromServer[] data;

}