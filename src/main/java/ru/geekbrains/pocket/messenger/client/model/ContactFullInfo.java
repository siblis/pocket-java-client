package ru.geekbrains.pocket.messenger.client.model;

import ru.geekbrains.pocket.messenger.database.entity.User;

public class ContactFullInfo {
    private User user;
    private int noReadMessage;

    public ContactFullInfo(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public int getNoReadMessage() {
        return noReadMessage;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setNoReadMessage(int noReadMessage) {
        this.noReadMessage = noReadMessage;
    }

    public void incNoReadMessage() {
        this.noReadMessage = getNoReadMessage() + 1;
    }

    @Override
    public String toString() {
        return getUser().getId() + " "
                + getUser().getAccount_name() + " "
//                + getUser().getEmail() + " "
                + (getNoReadMessage()==0?"":("  +"+getNoReadMessage()));
    }
}
