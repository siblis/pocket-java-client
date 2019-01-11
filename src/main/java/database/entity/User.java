package database.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "uid") // для совместимости с уже созданными БД
    private long user_id;

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

    public User(long user_id, String account_name, String email) {
        this.user_id = user_id;
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
        return user_id;
    }

    public void setUid(long id) {
        this.user_id = id;
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

    @Override
    public String toString() {
        return "User{" + "user_id=" + user_id + ", account_name=" + account_name + ", email=" + email + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (int) (this.user_id ^ (this.user_id >>> 32));
        hash = 41 * hash + Objects.hashCode(this.account_name);
        hash = 41 * hash + Objects.hashCode(this.email);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (this.user_id != other.user_id) {
            return false;
        }
        if (!Objects.equals(this.account_name, other.account_name)) {
            return false;
        }
        return Objects.equals(this.email, other.email);
    }
}
