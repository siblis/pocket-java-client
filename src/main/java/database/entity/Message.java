package database.entity;

import javax.persistence.*;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    private int id;

    @Column
    private String text;

    @Column
    private String time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    public Message() {}

    public Message(int id, String text, String time, User sender, User receiver) {
        this.id = id;
        this.text = text;
        this.time = time;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Message(int id, String text, String time) {
        this.id = id;
        this.text = text;
        this.time = time;
    }

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
