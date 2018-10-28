package database.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    private long id;

    @Column
    private String name;

    @Column
    private String email;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> sentMess;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> receivedMess;

    public User() {}

    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
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

    public void clearMessages(){
        sentMess.clear();
        receivedMess.clear();
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
