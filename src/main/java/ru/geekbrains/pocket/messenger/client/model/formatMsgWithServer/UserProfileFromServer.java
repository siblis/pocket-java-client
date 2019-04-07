package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import java.sql.Timestamp;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.geekbrains.pocket.messenger.database.entity.UserProfile;

@Getter
@NoArgsConstructor
public class UserProfileFromServer {
    private String id;

    private String username;

    private String fullname;

    private Date last_seen;

    public UserProfile toUserProfile() {
        return new UserProfile(id, username, fullname, 
                (last_seen == null ? null : new Timestamp(last_seen.getTime())));
    }

    public boolean isEmpty() {
        return (id + username + fullname).isEmpty() && last_seen == null;
    }
    
    @Override
    public String toString() {
        return "UserProfileFromServer{" + "id=" + id + ", username=" + username + 
                ", fullname=" + fullname + ", last_seen=" + last_seen + '}';
    }
}
