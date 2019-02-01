package client.model.formatMsgWithServer;

public class UserToServer {
    private String user;
    private String byname;

    public UserToServer(String userId, String byname) {
        this.user = user;
        this.byname = byname;
    }

    public String toJson() {
        return "{" +
                "\"user\": \"" + user + "\"," +
                "\"" + byname + "\"" +
                "}";
    }
}
