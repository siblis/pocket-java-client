package client;

import java.util.ArrayList;

public class User {
    public int id;
    public String nickName;
    private ArrayList<User> contactList;
    private ArrayList<Group> groupsList;

    public User(int id, String nickName) {
        this.id = id;
        this.nickName = nickName;
        contactList = new ArrayList<User>();
        groupsList = new ArrayList<Group>();
        contactList.add(this); // не уверен что нужно и что в этом месте
    }

}
