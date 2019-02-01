package database.entity;

import java.sql.Timestamp;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String id;

    @Column
    private String email;

    @OneToOne(mappedBy = "profile")
    private UserProfile profile;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> sentMess;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> receivedMess;

    public User() {
    }

    public User(String email, UserProfile profile) {
        this.id = profile.id;
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

    public String getUid() {
        return id;
    }

    public void setUid(String id) {
        this.id = id;
    }

    public String getAccount_name() {
        return profile.username;
    }

    public void setAccount_name(String name) {
        this.profile.username = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", email=" + email + ", profile=" + profile + '}';
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
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        return Objects.equals(this.profile, other.profile);
    }
    
    @Entity
    @Table(name = "profiles")
    public class UserProfile {
        
        @OneToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "profile_id")
        private String id;
        
        @Column
        private String username;
        
        @Column
        private String fullname;
        
        @Column
        private Timestamp last_seen;

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public Timestamp getLast_seen() {
            return last_seen;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 41 * hash + Objects.hashCode(this.id);
            hash = 41 * hash + Objects.hashCode(this.username);
            hash = 41 * hash + Objects.hashCode(this.fullname);
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
            final UserProfile other = (UserProfile) obj;
            if (!Objects.equals(this.id, other.id)) {
                return false;
            }
            if (!Objects.equals(this.username, other.username)) {
                return false;
            }
            return Objects.equals(this.fullname, other.fullname);
        }

        @Override
        public String toString() {
            return "UserProfile{" + "id=" + id + ", username=" + username + ", fullname=" + fullname + ", last_seen=" + last_seen + '}';
        }
    }
}
