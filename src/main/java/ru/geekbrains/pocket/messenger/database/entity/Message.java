package ru.geekbrains.pocket.messenger.database.entity;


import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    
    public enum MFields {
        id, text, time, sender, receiver
    }

    @Id
    @Column
    private String id;

    @Column
    private String text;

    @Column
    private Timestamp time;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    public Message(String text, Timestamp time) {
        this.text = text;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Message{" + "id=" + id + ", text=" + text + ", time=" + time + 
                ", sender=" + sender + ", receiver=" + receiver + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) &&
                Objects.equals(text, message.text) &&
                Objects.equals(time, message.time) &&
                Objects.equals(sender, message.sender) &&
                Objects.equals(receiver, message.receiver);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, text, time, sender, receiver);
    }
}