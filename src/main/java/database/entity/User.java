package database.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    private long uid;

    @Column
    private String account_name;

    @Column
    private String email;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> sentMess;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> receivedMess;

    public User() {
    }

    public User(long uid, String account_name, String email) {
        this.uid = uid;
        this.account_name = account_name;
        this.email = email;
        sentMess = new ArrayList<>();
        receivedMess = new ArrayList<>();
    }

    public void addReceivedMessage(Message message) {
        message.setReceiver(this);
        receivedMess.add(message);
    }

    public void addSentMessage(Message message) {
        message.setSender(this);
        sentMess.add(message);
    }

    public void clearMessages() {
        sentMess.clear();
        receivedMess.clear();
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long id) {
        this.uid = id;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String name) {
        this.account_name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
