package database.entity;

import javax.persistence.*;

@Entity
@Table(name = "MESSAGES")
public class Message {

    @Id
    private int id;

    @Column
    private String text;

    @Column
    private User sender;

    @Column
    private User receiver;

    public Message(int id, String text, User sender, User receiver) {
        this.id = id;
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Message() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }
}
