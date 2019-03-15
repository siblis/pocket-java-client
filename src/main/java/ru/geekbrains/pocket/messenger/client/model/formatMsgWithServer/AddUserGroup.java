package ru.geekbrains.pocket.messenger.client.model.formatMsgWithServer;

public class AddUserGroup {
    public String group_id;
    public String new_user_id;

    public AddUserGroup() {}

    public AddUserGroup(String group_id, String new_user_id) {
        this.group_id = group_id;
        this.new_user_id = new_user_id;
    }
}
