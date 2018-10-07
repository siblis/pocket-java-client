package client;

import java.util.ArrayList;

public class User {
    public int id;
   // public String nickName;
    private ArrayList<Integer> contactList;
    //private ArrayList<Group> groupsList;

    public User(int id, String nickName) {
        this.id = id;
        //this.nickName = nickName;
        //contactList = new ArrayList<User>();
        contactList = new ArrayList<Integer>();
        //groupsList = new ArrayList<Group>();
        contactList.add(this.id); // не уверен что нужно и что в этом месте
    }

    public void addContact (int uid){
        contactList.add(uid);
    }
}
