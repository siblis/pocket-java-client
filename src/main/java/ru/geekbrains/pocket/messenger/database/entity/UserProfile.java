package ru.geekbrains.pocket.messenger.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.messenger.client.model.pub.UserProfilePub;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String uid; //serverUserId

    //@NotNull
    @Column
    private String username;

    @Column
    private String fullname;

    @Column(name = "last_seen")
    private Timestamp lastSeen;

    @OneToOne(mappedBy = "profile")
    private User user;

    public UserProfile(@NotNull UserProfile userProfile) {
        this.id = userProfile.getId();
        this.uid = userProfile.getUid();
        this.username = userProfile.getUsername();
        this.fullname = userProfile.getFullname();
        this.lastSeen = userProfile.getLastSeen();
        this.user = userProfile.getUser();
    }

    public UserProfile(@NotNull UserProfilePub userProfilePub) {
        this.uid = userProfilePub.getId();
        this.username = userProfilePub.getUsername();
        this.fullname = userProfilePub.getFullname();
        this.lastSeen = userProfilePub.getLastSeen() == null ? null :
                new Timestamp(userProfilePub.getLastSeen().getTime());
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
        return "UserProfile{" + "id=" + id + ", username=" + username + ", fullname=" + fullname + ", last_seen=" + lastSeen + '}';
    }

}
