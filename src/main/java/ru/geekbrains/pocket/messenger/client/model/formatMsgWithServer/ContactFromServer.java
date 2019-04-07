package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.geekbrains.pocket.messenger.database.entity.User;
import ru.geekbrains.pocket.messenger.database.entity.UserProfile;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
public class ContactFromServer {

    private String byname;
    private Timestamp added_at;
    private UserProfileFromServer contact;

    public User toUser() {
        return new User(null, toUserProfile());
    }

    public UserProfile toUserProfile() {
        return contact.toUserProfile();
    }
}