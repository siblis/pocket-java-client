package client.model;

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
}
