package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class AddGroup {
    private String name;
    private String description;

    @SerializedName("public")
    private boolean isPublic;



    public AddGroup(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
