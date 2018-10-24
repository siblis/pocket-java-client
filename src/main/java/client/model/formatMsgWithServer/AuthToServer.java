package client.model.formatMsgWithServer;

public class AuthToServer {
    public String account_name;
    public String password;

    public AuthToServer() {

    }

    public AuthToServer(String account_name, String password) {
        this.account_name = account_name;
        this.password = password;
    }
}
