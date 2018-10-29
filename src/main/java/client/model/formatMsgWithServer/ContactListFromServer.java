package client.model.formatMsgWithServer;

public class ContactListFromServer {
    private long id;
    private String name;

    public ContactListFromServer() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
