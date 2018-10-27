package client.model.formatMsgWithServer;

public class RegToServer {
    public String account_name;
    public String email;
    public String password;

    public RegToServer(String account_name, String email, String password) {
        this.account_name = account_name;
        this.email = email;
        this.password = password;
    }
}
