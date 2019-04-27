package ru.geekbrains.pocket.messenger.database.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    public enum UFields {
        id, email, createdAt, profile, sentMess, receivedMess
    }
    
    @Id
    @Column
    private String id; //serverUserId

    //@NotNull
    //TODO index unique
    @Column
    private String email; 
    //(на 06.04.2019) для всех (кроме текущего аккаунта), часть адреса закрыта звёздочками

    @Column
    private Timestamp createdAt = new Timestamp(new Date().getTime());

    @OneToOne(cascade = CascadeType.ALL, 
            orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private UserProfile profile;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, 
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Message> sentMess;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, 
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Message> receivedMess;

    public User(String email, UserProfile profile) {
        this.id = profile.getId();
        this.email = email;
        this.profile = profile;
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

    public String getUserName() {
        return profile.getUserName();
    }

    public void setUserName(String name) {
        if (profile == null) profile = new UserProfile();
        profile.setUserName(name);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", email=" + email + ", " + 
                (profile == null ? "UserProfile=null" : profile) + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.email);
        hash = 41 * hash + Objects.hashCode(this.profile);
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
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.profile, other.profile)) {
            return false;
        }
        //email последний, т.к. данные (на 06.04.2019) могут быть не полными
        //(на 06.04.2019) для всех (кроме текущего аккаунта), часть адреса закрыта звёздочками
        return Objects.equals(this.email, other.email);
    }

}
