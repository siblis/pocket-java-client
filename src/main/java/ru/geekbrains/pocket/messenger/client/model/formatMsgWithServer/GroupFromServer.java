package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.geekbrains.pocket.messenger.database.entity.Group;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupFromServer {

    private String id;

    private String creator;

    private String space;

    private String name;

    private String description;

    private String invitation_code; //Виден только создателю группы и только если активирован

    @SerializedName("public")
    private boolean isPublic;


    public Group toGroup() {
        return new Group(id, creator, space, name, description, invitation_code, isPublic);
    }

    @Override
    public String toString() {
        return "GroupFromServer{" +
                "id=" + id +
                ", creator=" + creator +
                ", space=" + space +
                ", name=" + name +
                ", description=" + description +
                ", invitation_code=" + invitation_code +
                ", isPublic=" + isPublic +
                '}';
    }
}
