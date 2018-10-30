package client.model;

import java.util.ArrayList;

public class User {
    private String uid;
    private String account_name;
    private String email;
    private ArrayList<User> contactList;

    public User(String uid, String account_name) {
        this.uid = uid;
        this.account_name = account_name;
    }
    public User(String uid, String account_name, String email) {
        this.uid = uid;
        this.account_name = account_name;
        this.email = email;
    }
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
