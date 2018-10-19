package client;

import java.util.ArrayList;

public class User {
    private String contact;// email
    private String id;
    private String name;
    public ArrayList<User> contactList;
    //private ArrayList<Group> groupsList;

    public User(String contact) {
        this.contact = contact;
    }

    public User(String contact, String id, String name) {
        this.contact = contact;
        this.id = id;
        this.name = name;
        contactList = new ArrayList<User>();
    }
}
