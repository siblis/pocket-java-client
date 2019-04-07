package ru.geekbrains.pocket.messenger.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "userprofiles")
public class UserProfile {
    
    public enum UPFields {
        id, userName, fullName, lastSeen
    }

    @Id
    @Column
    private String id; //serverUserId

    @Column
    private String userName;

    @Column
    private String fullName;

    @Column
    private Timestamp lastSeen;

    @OneToOne(mappedBy = "profile")
    private User user;

    public UserProfile(String id, String userName, String fullName, Timestamp lastSeen) {
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.lastSeen = lastSeen;
    }

    public UserProfile(@NotNull UserProfile userProfile) {
        this.id = userProfile.getId();
        this.userName = userProfile.getUserName();
        this.fullName = userProfile.getFullName();
        this.lastSeen = userProfile.getLastSeen();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.userName);
        hash = 41 * hash + Objects.hashCode(this.fullName);
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
        if (!Objects.equals(this.userName, other.userName)) {
            return false;
        }
        return Objects.equals(this.fullName, other.fullName);
    }

    @Override
    public String toString() {
        return "UserProfile{" + "id=" + id + ", userName=" + userName + 
                ", fullName=" + fullName + ", lastSeen=" + lastSeen + '}';
    }

}
