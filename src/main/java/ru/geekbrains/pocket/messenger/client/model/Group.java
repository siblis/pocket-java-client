package ru.geekbrains.pocket.messenger.client.model;

import java.util.List;

public class Group {
    private String gid;
    private String group_name;
    private List<Integer> users;

    public Group() {}

    public void setGid(String gid) {
        this.gid = gid;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public void setUsers(List<Integer> users) {
        this.users = users;
    }

    public String getGid() {
        return gid;
    }

    public String getGroup_name() {
        return group_name;
    }

    public List<Integer> getUsers() {
        return users;
    }
}
