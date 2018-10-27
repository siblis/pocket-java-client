package client.model;

import java.util.ArrayList;

public class User {
    public String uid;

    public User(String uid, String account_name) {
        this.uid = uid;
        this.account_name = account_name;
    }

    public String account_name;
    public String email;
    public ArrayList<User> contactList;
    //private ArrayList<Group> groupsList;

    public String getUid() {
        return uid;
    }

    public String getAccount_name() {
        return account_name;
    }

    public String getEmail() {
        return email;
    }
}
