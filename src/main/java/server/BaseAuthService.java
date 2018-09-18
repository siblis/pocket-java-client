package server;

import java.util.ArrayList;
import java.util.List;

public class BaseAuthService extends AuthService {
    private class Entry {
        private String login;
        private String password;
        private String nick;

        public Entry(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getNick() {
            return nick;
        }
    }

    private List<Entry> entries;

    public BaseAuthService() {
        entries = new ArrayList<>();
        entries.add(new Entry("login1", "pass1", "nick1"));
        entries.add(new Entry("login2", "pass2", "nick2"));
        entries.add(new Entry("login3", "pass3", "nick3"));
    }

    @Override
    public String getNick(String login, String pass) {
        for (Entry e : entries) {
            if (e.getLogin().equals(login) && e.getPassword().equals(pass)) return e.getNick();
        }
        return null;
    }

    @Override
    public boolean login(String login, String pass) {
        for (Entry e : entries) {
            if (e.getLogin().equals(login) && e.getPassword().equals(pass)) return true;
        }
        return false;
    }

    @Override
    public boolean contains(String userName) {
        if (userName == null || userName.trim().isEmpty()) return false;

        for (Entry e : entries) {
            if (userName.equals(e.getNick())) return true;
        }
        return false;
    }
}