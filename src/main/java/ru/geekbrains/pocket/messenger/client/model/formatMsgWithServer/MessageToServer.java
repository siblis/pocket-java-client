package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
/**
 * Для отправки сообщения, либо получателю ({@code recipient != null} & 
 * {@code group == null}), либо в группу ({@code recipient == null} & 
 * {@code group != null}).
 *
 * @param text текст сообщения
 * @param groupId id группы (для сообщения в группу)
 * @param recipientId id получателя (для личного сообщения)
 * @param messageId временное id сообщения!?
 */
@AllArgsConstructor
public class MessageToServer {
    private String text;
    private String group;
    private String recipient;
    private String messageId;

//    public MessageToServer(String text, String groupId, String recipientId, String messageId) {
//        this.text = text;
//        this.group = groupId;
//        this.recipient = recipientId;
//        this.messageId = messageId;
//    }
}