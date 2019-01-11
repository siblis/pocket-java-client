package client.model.formatMsgWithServer;

public class ContactListFromServer {
    private long user_id;
    private String account_name;
    private String email;
    private String status;

    public ContactListFromServer() {}

    public long getId() {
        return user_id;
    }

    public void setId(long user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return account_name;
    }

    public void setName(String name) {
        this.account_name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStatusOnline() {
        return status.equals("online");
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
