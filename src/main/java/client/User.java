package client;

import java.util.ArrayList;

public class User {
    public String email;
    public String uid;
    public String account_name;
    public ArrayList<User> contactList;
    //private ArrayList<Group> groupsList;

    public User(String email) {
        this.email = email;
    }

    public User(String email, String uid, String account_name) {
        this.email = email;
        this.uid = uid;
        this.account_name = account_name;
        contactList = new ArrayList<User>();
    }
}
