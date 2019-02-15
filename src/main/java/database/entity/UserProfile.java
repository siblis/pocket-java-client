package database.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

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
