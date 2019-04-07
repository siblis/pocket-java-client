package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.geekbrains.pocket.messenger.database.entity.User;

@Getter
@NoArgsConstructor
public class UserFromServer {
    private String id;
    private String email;
    private UserProfileFromServer profile;
    
    public User toUser() {
        return new User(email, profile.toUserProfile());
    }

    @Override
    public String toString() {
        return "UserFromServer{" + "id=" + id + ", email=" + email + ", " + 
                (profile == null ? "UserProfileFromServer=null" : profile) + '}';
    }
}
