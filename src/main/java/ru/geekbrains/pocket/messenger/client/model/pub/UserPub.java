package ru.geekbrains.pocket.messenger.client.model.pub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPub {
    private String id;

    @NotEmpty
    private String email;

    private UserProfilePub profile;

//    public UserPub(@NotNull User user) {
//        this.id = user.getId().toString();
//        this.email = user.getEmail();
//        this.profile = new UserProfilePub(user);
//    }

    @Override
    public String toString() {
        return "User{" +
                "'id':'" + id + "'" +
                ", 'email':'" + email + "'" +
                ", " + profile +
                '}';
    }

}