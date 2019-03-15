package ru.geekbrains.pocket.messenger.client.model.pub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfilePub {
    private String id;

    private String username;

    private String fullname;

    @JsonProperty("last_seen")
    private Date lastSeen;

//    public UserProfilePub(@NotNull User user) {
//        this.id = user.getId().toString();
//        this.username = user.getProfile().getUsername();
//        this.fullname = user.getProfile().getFullName();
//        //this.lastSeen = null;
//    }

    @Override
    public String toString() {
        return "'profile':{" +
                "'id':'" + id + "'" +
                ", 'username':" + printField(fullname) +
                ", 'fullname':" + printField(fullname) +
                '}';
    }

    private String printField(String field) {
        return field == null ? "null" : ("'" + field + "'");
    }
}
