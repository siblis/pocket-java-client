package client;

import java.util.ArrayList;

public class User {
    public String contact;// id или email в зависимости от REST
   // public String nickName;
    //private ArrayList<Integer> contactList;
    //private ArrayList<Group> groupsList;

    public User(String contact) {
        this.contact = contact;
        //this.nickName = nickName;
        //contactList = new ArrayList<User>();
        //contactList = new ArrayList<Integer>();
        //groupsList = new ArrayList<Group>();
       // contactList.add(this.id); // не уверен что нужно и что в этом месте
    }

    public String getContact() {
        return contact;
    }
//    public void addContact (int uid){
//        contactList.add(uid);
//    }
}
